package scolabs.com.tenine.model;

import com.activeandroid.Configuration;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import javax.validation.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.*;

import java.util.Date;

/**
 * Created by scolary on 2/6/2016.
 */
@Table(name = "User")
public class User extends Model {

    @NotNull(message = "Username is required")
    @Column
    @Size(min = 4, max = 20, message = "username, min length = 4 and max = 20")
    String username;

    @NotNull(message = "Email required")
    @Column
    @Pattern(regexp="^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6}$", message = "Not a valid email")
    String email;

    @Column
    String password;
    @Column
    Date date_created;
    @Column
    Date last_modified;
    @Column
    long serverId;

    public User(){

    }

    public User(String username, String email) {
        super();
        this.username = username;
        this.email = email;
    }

    public static User getDbUser(String username, String email) {
        if(!username.equals(""))
        {
            return new Select()
                    .from(User.class)
                    .where("username = ?", username)
                    .executeSingle();
        }
        else
        {
            return new Select()
                    .from(User.class)
                    .where("email = ?", email)
                    .executeSingle();
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getDate_created() {
        return date_created;
    }

    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }

    public Date getLast_modified() {
        return last_modified;
    }

    public void setLast_modified(Date last_modified) {
        this.last_modified = last_modified;
    }

    public long getServerId() {
        return serverId;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }
}

