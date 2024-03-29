package scolabs.com.tenine.remoteOperations;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.delay.packet.DelayInformation;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Date;

import scolabs.com.tenine.databaseQueries.CommentQueries;
import scolabs.com.tenine.databaseQueries.ShowQueries;
import scolabs.com.tenine.model.Comment;
import scolabs.com.tenine.model.Show;
import scolabs.com.tenine.services.AppNotificationManager;
import scolabs.com.tenine.utils.Global;
import scolabs.com.tenine.utils.GlobalSettings;

/**
 * Created by scolary on 4/7/2016.
 */
public class ChatSettings {
    //static final String DOMAIN = "scolabs.com";
    //protected static final String DOMAIN = "192.168.43.174";
    protected static final String DOMAIN = "10.0.2.2";
    protected static final String SERVER_NAME = "msi";
    protected static final int PORT = 5222;
    protected static final String SERVICE = "xmpp";
    protected static XMPPTCPConnection mConnection;
    protected static MultiUserChat muc2;
    protected MultiUserChatManager manager;
    protected Context mContext;
    public static final String BROADCAST = "com.scolabs.tenine.android.action.broadcast";

    public void createConnection(final String USER_ID, final String key, final Context cxt) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
                config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                config.setUsernameAndPassword(USER_ID, key);
                config.setServiceName(SERVICE);
                config.setHost(DOMAIN);
                config.setPort(PORT);
                config.setResource(USER_ID + "@" + DOMAIN + "/Smack");
                config.setDebuggerEnabled(true);
                //config.setSocketFactory(SSLSocketFactory.getDefault());
                SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
                SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
                mConnection = new XMPPTCPConnection(config.build());
                mContext = cxt;

                try {
                    if(GlobalSettings.checkConnection(mContext))
                    {
                        try{
                            mConnection.connect();
                        }catch (SocketTimeoutException | SmackException.ConnectionException ex) {

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast toast = Toast.makeText(mContext, "Couldn't Connect \n" +
                                            "Connection Timeout...", Toast.LENGTH_LONG);

                                    toast.getView().setBackgroundColor(Color.RED);
                                    toast.show();
                                    Log.e("SocketTimeoutException",ex.getMessage());
                                }
                            });
                        }
                        try{
                            mConnection.login(USER_ID, key);
                            Presence presence = new Presence(Presence.Type.available);
                            mConnection.sendPacket(presence);
                            ChatManager chatManager = ChatManager.getInstanceFor(mConnection);
                            chatManager.addChatListener(new ChatManagerListenerImpl());
                        }catch (final SmackException.NotConnectedException ex){
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast toast = Toast.makeText(mContext,
                                            "You are not, or no longer, connected",Toast.LENGTH_LONG);
                                    toast.getView().setBackgroundColor(Color.RED);
                                    toast.show();
                                    Log.e("NotConnectedException", ex.getMessage());
                                }
                            });
                        }


                        Log.w("Jabber Connection", "Connection successful");

                    } else {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast m = Toast.makeText(mContext,
                                        "Check Your Internet Connection",Toast.LENGTH_LONG);
                                m.getView().setBackgroundColor(Color.RED);
                                m.show();
                            }
                        });
                    }
                }
                catch (SmackException | IOException | XMPPException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void sendMessage(String to, String comment, long showId) {
        //Message msg = new Message("empire@conference.server1", Message.Type.groupchat);
        //String room = to.concat("@conference."+SERVER_NAME);
        String conf = to.concat("@conference.msi");
        Message msg = new Message(conf, Message.Type.groupchat);
        msg.setBody(comment);
        //msg.setSubject("" + new Date().getTime());
        try {
            manager = MultiUserChatManager.getInstanceFor(mConnection);
            muc2 = manager.getMultiUserChat(conf);
            groupChat(conf, showId);
            muc2.sendMessage(msg);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public synchronized void groupChat(String showRoomAddress, final long showId) throws Exception {
        manager = MultiUserChatManager.getInstanceFor(mConnection);
        //muc2 = manager.getMultiUserChat("empire@conference.server1");
        muc2 = manager.getMultiUserChat(showRoomAddress);
        muc2.join(GlobalSettings.getLoginUser().getUsername());
        muc2.addMessageListener(new MessageListener() {
            @Override
            public void processMessage(Message message) {
                Date date_sent = new Date();
                String nickName = GlobalSettings.getNickNameFromAddress(message.getFrom());
                DelayInformation inf = null;
                try{
                    inf = message.getExtension("delay","urn:xmpp:delay");
                }catch(Exception ex){ex.printStackTrace();}
                if(inf != null){
                   date_sent  = inf.getStamp();
                }

                try {
                    final Comment cmt = new Comment(message.getBody(), nickName, date_sent, showId);
                    cmt.setStanzaId(message.getStanzaId());
                    int cmt_count = CommentQueries.getStanzaIdCount(message.getStanzaId());
                    if (cmt_count == 0 || cmt_count == -1) {
                        cmt.save();
                        broadcastEvent("increment_comment",showId);
                        notifyNewComment(cmt, showId);
                        handleScrollingList(cmt);
                        Log.e("Roster", message != null ? message.getBody() + " From : " + message : null);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void broadcastEvent(String type, long showId) {
        Intent intent = new Intent(BROADCAST);
        intent.setAction(type);
        intent.putExtra("showId",showId);
        mContext.sendBroadcast(intent);
    }

    public void notifyNewComment(final Comment cmt, long showId) {
        final Show show_commented = ShowQueries.getShowById(showId);
        final String show_name = show_commented.getName();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                String title_msg = show_name.concat(": new comment");
                String user_comment = cmt.getCommentator() + ": \n" + cmt.getContent();
                if(GlobalSettings.getBoolValue(mContext,"new_comment"))
                {
                    new AppNotificationManager(show_commented).
                            displayNotificationOne(user_comment, title_msg, "newComment", mContext);
                }
            }
        });
    }

    public void handleScrollingList(final Comment cmt) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                String userName = GlobalSettings.getLoginUser().getUsername();
                Global.cmAdapter.addCommentToList(cmt);
                Global.lsView.setStackFromBottom(true);
                if (cmt.getCommentator().equals(userName)) {//Go to Bottom of the list when sending...
                    Global.lsView.setSelection(Global.lsView.getCount() - 1);
                    Global.lsView.refreshDrawableState();
                } else if (Global.lsView.getLastVisiblePosition() == Global.lsView.getCount() - 2)//Go to Bottom of the list when the last element is visible
                {
                    Global.lsView.setSelection(Global.lsView.getCount() - 1);
                    Global.lsView.refreshDrawableState();
                }
            }
        });
    }


    private class ChatManagerListenerImpl implements ChatManagerListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void chatCreated(final Chat chat, final boolean createdLocally) {
            chat.addMessageListener(new ChatMessageListener() {
                @Override
                public void processMessage(Chat chat, Message message) {
                    System.out.println("Received message: "
                            + (message != null ? message.getBody() : "NULL"));
                    Log.e("Message From", chat.getParticipant());
                }
            });
        }
    }

}
