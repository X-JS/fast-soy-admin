package com.fastsoy.common.context;

import com.fastsoy.common.dbo.UserDBO;

public class UserContext {

    private static final ThreadLocal<UserDBO> HOLDER = new ThreadLocal<UserDBO>();

    public static void set(UserDBO user) {
        HOLDER.set(user);
    }

    public static UserDBO get() {
        return HOLDER.get();
    }

    public static Long getUserId() {
        UserDBO user = HOLDER.get();
        return user != null ? user.getId() : null;
    }

    public static void clear() {
        HOLDER.remove();
    }
}
