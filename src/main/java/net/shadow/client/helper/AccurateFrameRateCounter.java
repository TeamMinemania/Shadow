/*
 * Copyright (c) Shadow client, 0x150, Saturn5VFive 2022. All rights reserved.
 */

package net.shadow.client.helper;

import java.util.ArrayList;
import java.util.List;

public class AccurateFrameRateCounter {
    public static final AccurateFrameRateCounter globalInstance = new AccurateFrameRateCounter();
    List<Long> records = new ArrayList<>();

    public void recordFrame() {
        long c = System.currentTimeMillis();
        records.add(c);
        records.removeIf(aLong -> aLong + 1000 < c);
    }

    public int getFps() {
        return records.size();
    }
}
