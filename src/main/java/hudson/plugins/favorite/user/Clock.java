package hudson.plugins.favorite.user;

import com.google.common.annotations.VisibleForTesting;

class Clock {
    private final Long time;

    public Clock() {
        this(Long.MIN_VALUE);
    }

    @VisibleForTesting
    public Clock(Long time) {
        this.time = time;
    }

    public long getTime() {
        return time == Long.MIN_VALUE ? System.currentTimeMillis() : time;
    }
}
