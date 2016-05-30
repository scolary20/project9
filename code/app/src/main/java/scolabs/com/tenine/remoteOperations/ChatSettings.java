package scolabs.com.tenine.remoteOperations;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.Occupant;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.SSLSocketFactory;

import scolabs.com.tenine.databaseQueries.CommentQueries;
import scolabs.com.tenine.model.Comment;
import scolabs.com.tenine.utils.Global;
import scolabs.com.tenine.utils.GlobalSettings;

/**
 * Created by scolary on 4/7/2016.
 */
public class ChatSettings {
    //static final String DOMAIN = "scolabs.com";
    static final String DOMAIN = "192.168.43.174";
    static final String SERVER_NAME = "msi";
    static final int PORT = 5222;
    static final String SERVICE = "xmpp";
    static XMPPTCPConnection mConnection;
    static MultiUserChat muc2;
    MultiUserChatManager manager;

    public void createConnection(final String USER_ID, final String key, final Context mContext) {
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


                try {
                    ConnectivityManager connMgr = (ConnectivityManager)
                            mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        mConnection.connect();
                        mConnection.login(USER_ID, key);
                        Presence presence = new Presence(Presence.Type.available);
                        mConnection.sendPacket(presence);
                        ChatManager chatManager = ChatManager.getInstanceFor(mConnection);
                        chatManager.addChatListener(new ChatManagerListenerImpl());
                        Log.e("Jabber Connection", "Connection successful");

                    } else {
                        // display error
                    }

                } catch (SmackException | IOException | XMPPException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void sendMessage(String to, String comment) {
        //Message msg = new Message("empire@conference.server1", Message.Type.groupchat);
        //String room = to.concat("@conference."+SERVER_NAME);
        String conf = to.concat("@conference.msi");
        Message msg = new Message(conf, Message.Type.groupchat);
        //Message msg = new Message("tsent@server1", Message.Type.chat);
        msg.setBody(comment);
        try {
            groupChat(msg);
            manager = MultiUserChatManager.getInstanceFor(mConnection);
            muc2 = manager.getMultiUserChat("empire@conference.msi");
            FormField date_created = new FormField("msg_date_created");
            date_created.addValue("" + new Date().getTime());
            muc2.getConfigurationForm().addField(date_created);
            muc2.sendMessage(msg);
            //mConnection.sendPacket(msg);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void groupChat(Message msg) throws Exception {
        manager = MultiUserChatManager.getInstanceFor(mConnection);
        //muc2 = manager.getMultiUserChat("empire@conference.server1");
        muc2 = manager.getMultiUserChat("empire@conference.msi");
        muc2.join(GlobalSettings.getLoginUser().getUsername());
        muc2.addMessageListener(new MessageListener() {
            @Override
            public void processMessage(Message message) {
                Date date = null;
                Global.cmAdapter.setNotifyOnChange(true);
                try {
                    //Log.e("date error", long_value);
                    // date = new Date(Long.parseLong(long_value));
                    Comment cmt = new Comment(message.getBody(), muc2.getNickname(), new Date(), Global.showId);
                    cmt.setStanzaId(message.getStanzaId());
                    int cmt_count = CommentQueries.getStanzaIdCount(message.getStanzaId());
                    if (cmt_count == 0 || cmt_count == -1) {
                        cmt.save();
                        Global.cmAdapter.add(cmt);
                        Global.cmAdapter.notifyDataSetChanged();
                        Log.e("Roster", message != null ? message.getBody() + " From : " + message : null);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        //muc2.sendMessage(msg);
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
