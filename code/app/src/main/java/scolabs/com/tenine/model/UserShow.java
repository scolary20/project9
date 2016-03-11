package scolabs.com.tenine.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;

/**
 * Created by scolary on 3/1/2016.
 */
public class UserShow extends Model {
    @Column
    private long userId;
    @Column(index = true, unique = true)
    private long showId;

    public UserShow() {

    }

    public UserShow(long userId, long showId) {
        this.userId = userId;
        this.showId = showId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getShowId() {
        return showId;
    }

    public void setShowId(long showId) {
        this.showId = showId;
    }
}
