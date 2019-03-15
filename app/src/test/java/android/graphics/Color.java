package android.graphics;

public class Color {
    public static int rgb(int red, int green, int blue) {
        return 0xff000000 | (red << 16) | (green << 8) | blue;
    }
}
