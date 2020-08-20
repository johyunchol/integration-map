package kr.co.kkensu.integrationmap;

public enum AnchorType {
    /**
     0,0      0.5,0.0        1,0
     *-----+-----+-----+-----*
     |     |     |     |     |
     |     |     |     |     |
     0,0.5 +-----+-----+-----+-----+ 1,0.5
     |     |     |   X |     |           (U, V) = (0.7, 0.6)
     |     |     |     |     |
     *-----+-----+-----+-----*
     0,1      0.5,1.0        1,1
     */

    TYPE_DEFAULT(1),
    TYPE_CENTER_BOTTOM(1),
    TYPE_CENTER_CENTER(2),
    TYPE_CENTER_TOP(3),
    TYPE_CENTER_TOP_20(4);

    public int value;

    AnchorType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static AnchorType fromValue(int value) {
        for (AnchorType state : AnchorType.values()) {
            if (state.value == value) {
                return state;
            }
        }
        return TYPE_DEFAULT;
    }
}
