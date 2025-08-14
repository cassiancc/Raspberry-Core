package cc.cassian.raspberry.compat.controllable;

import com.mrcrayfish.controllable.client.ButtonBindings;

public class ControllableCompat {
    public static boolean isJumping() {
         return ButtonBindings.JUMP.isButtonDown();
    }
}
