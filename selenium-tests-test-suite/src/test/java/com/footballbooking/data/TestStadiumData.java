package com.footballbooking.data;

/**
 * Dữ liệu trích từ dump bảng stadium (bookingfootball).
 * Dùng để assert/filter theo dữ liệu thật.
 */
public final class TestStadiumData {
    private TestStadiumData() {
    }

    // From dump: stadium id=1
    public static final long STADIUM_ID_1 = 1L;
    public static final String STADIUM_NAME_1 = "Sân số 1";
    public static final String AREA_QUY_NHON = "Quy Nhơn";
    public static final String FIELD_TYPE_SAN_5 = "Sân 5";

    // Another real stadium with different area/type
    public static final long STADIUM_ID_3 = 3L;
    public static final String AREA_TAY_SON = "Tây Sơn";
    public static final String FIELD_TYPE_SAN_11 = "Sân 11";
}
