package kr.co.kkensu.integrationmap;

/**
 * Created by admin on 2017-01-16.
 */
public enum MapType {
    GOOGLE_MAP("Google Map"),
    KAKAO_MAP("Kakao Map"),
    NAVER_MAP("Naver Map");

    private String name;

    MapType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
