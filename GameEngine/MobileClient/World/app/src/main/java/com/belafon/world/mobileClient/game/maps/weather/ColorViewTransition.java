package com.belafon.world.mobileClient.game.maps.weather;

public abstract class ColorViewTransition {

    // returns color, that will be summed with the current color
    public abstract Color getColorUpdate();

    public abstract void updateCurrentIdleCount();

    public abstract boolean isTransitionDone();
    
    public static class Color {
        public final int r;
        public final int g;
        public final int b;
        public final int a;

        public Color(int r, int g, int b, int a) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }
    }
}
