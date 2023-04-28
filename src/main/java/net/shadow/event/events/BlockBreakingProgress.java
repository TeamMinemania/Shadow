//     _____ _               _               
//    / ____| |             | |              
//   | (___ | |__   __ _  __| | _____      __
//    \___ \| '_ \ / _` |/ _` |/ _ \ \ /\ / /
//    ____) | | | | (_| | (_| | (_) \ V  V / 
//   |_____/|_| |_|\__,_|\__,_|\___/ \_/\_/  
//                                           
//                                           
package net.shadow.event.events;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.shadow.event.base.Event;
import net.shadow.event.base.Listener;

import java.util.ArrayList;

public interface BlockBreakingProgress extends Listener {
    void onBlockBreakingProgress(BlockBreakingProgressEvent event);

    class BlockBreakingProgressEvent
            extends Event<BlockBreakingProgress> {
        private final BlockPos blockPos;
        private final Direction direction;

        public BlockBreakingProgressEvent(BlockPos blockPos,
                                          Direction direction) {
            this.blockPos = blockPos;
            this.direction = direction;
        }

        @Override
        public void call(ArrayList<BlockBreakingProgress> listeners) {
            for (BlockBreakingProgress listener : listeners)
                listener.onBlockBreakingProgress(this);
        }

        @Override
        public Class<BlockBreakingProgress> getThisType() {
            return BlockBreakingProgress.class;
        }

        public BlockPos getBlockPos() {
            return blockPos;
        }

        public Direction getDirection() {
            return direction;
        }
    }
}
