package com.vn.test.bshoes.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * These pin the two ways the server-side gate can fail badly: letting an
 * unauthenticated caller into an admin endpoint, or locking a customer out of
 * the public storefront.
 */
class ScreenPermissionsTest {

    // ---- public vs guarded ------------------------------------------------

    @Test
    void loginAndPingArePublic() {
        assertTrue(ScreenPermissions.isPublic("POST", "/api/auth/login"));
        assertTrue(ScreenPermissions.isPublic("GET", "/api/ping"));
    }

    @Test
    void clientLogSinkIsPublicSoLogsStillArriveWhenLoggedOut() {
        assertTrue(ScreenPermissions.isPublic("POST", "/api/logs/client"));
    }

    @Test
    void storefrontBrowsingIsPublic() {
        assertTrue(ScreenPermissions.isPublic("GET", "/api/san-pham"));
        assertTrue(ScreenPermissions.isPublic("GET", "/api/san-pham/12"));
        assertTrue(ScreenPermissions.isPublic("GET", "/api/san-pham-chi-tiet/store"));
        assertTrue(ScreenPermissions.isPublic("GET", "/api/loai-san-pham"));
    }

    @Test
    void customerCartAndCheckoutArePublic() {
        assertTrue(ScreenPermissions.isPublic("POST", "/api/gio-hang/checkout"));
        assertTrue(ScreenPermissions.isPublic("GET", "/api/gio-hang/don-hang"));
    }

    @Test
    void placingAPreOrderIsPublicButManagingThemIsNot() {
        assertTrue(ScreenPermissions.isPublic("POST", "/api/dat-truoc"));
        assertFalse(ScreenPermissions.isPublic("GET", "/api/dat-truoc"));
        assertFalse(ScreenPermissions.isPublic("DELETE", "/api/dat-truoc/5"));
    }

    @Test
    void adminEndpointsAreNotPublic() {
        assertFalse(ScreenPermissions.isPublic("GET", "/api/nhan-vien"));
        assertFalse(ScreenPermissions.isPublic("GET", "/api/thong-ke/hom-nay"));
        assertFalse(ScreenPermissions.isPublic("POST", "/api/san-pham-ql"));
        assertFalse(ScreenPermissions.isPublic("GET", "/api/khach-hang"));
    }

    @Test
    void publicProductPrefixDoesNotLeakTheAdminProductEndpoint() {
        // "/api/san-pham" must not swallow "/api/san-pham-ql"
        assertFalse(ScreenPermissions.isPublic("GET", "/api/san-pham-ql"));
        assertFalse(ScreenPermissions.isPublic("GET", "/api/san-pham-ql/recycle"));
    }

    @Test
    void nonApiPathsAreLeftAlone() {
        assertTrue(ScreenPermissions.isPublic("GET", "/login"));
        assertTrue(ScreenPermissions.isPublic("GET", "/assets/index.js"));
    }

    // ---- path -> screen ---------------------------------------------------

    @Test
    void mapsApiPathsToTheScreenThatGuardsThem() {
        assertEquals("dashboard", ScreenPermissions.screenFor("/api/thong-ke/hom-nay"));
        assertEquals("san-pham", ScreenPermissions.screenFor("/api/san-pham-ql"));
        assertEquals("san-pham", ScreenPermissions.screenFor("/api/san-pham-chi-tiet/7"));
        assertEquals("hoa-don", ScreenPermissions.screenFor("/api/hoa-don/3/thanh-toan"));
        assertEquals("hoa-don", ScreenPermissions.screenFor("/api/hoa-don-chi-tiet/by-hoa-don/3"));
        assertEquals("nhan-vien", ScreenPermissions.screenFor("/api/nhan-vien/quyen"));
        assertEquals("nhan-vien", ScreenPermissions.screenFor("/api/vai-tro"));
        assertEquals("khach-hang", ScreenPermissions.screenFor("/api/dia-chi/4"));
        assertEquals("lich-su", ScreenPermissions.screenFor("/api/lich-su-hoa-don"));
        assertEquals("bao-hanh", ScreenPermissions.screenFor("/api/bao-hanh/2/trang-thai"));
        assertEquals("phieu-giam-gia", ScreenPermissions.screenFor("/api/phieu-giam-gia"));
        assertEquals("dat-truoc", ScreenPermissions.screenFor("/api/dat-truoc/1/chuyen-don"));
    }

    @Test
    void longestPrefixWinsSoHoaDonChiTietIsNotTreatedAsHoaDon() {
        // both prefixes match; the more specific one must decide
        assertEquals("hoa-don", ScreenPermissions.screenFor("/api/hoa-don-chi-tiet/1"));
    }

    @Test
    void sharedLookupsHaveNoOwningScreen() {
        assertNull(ScreenPermissions.screenFor("/api/chat-lieu"));
        assertNull(ScreenPermissions.screenFor("/api/mau-sac"));
        assertNull(ScreenPermissions.screenFor("/api/upload"));
    }

    // ---- quyền check ------------------------------------------------------

    @Test
    void starGrantsEveryScreen() {
        assertTrue(ScreenPermissions.allows("*", "nhan-vien"));
        assertTrue(ScreenPermissions.allows(" * ", "dashboard"));
    }

    @Test
    void csvGrantsOnlyTheListedScreens() {
        assertTrue(ScreenPermissions.allows("hoa-don,khach-hang", "hoa-don"));
        assertTrue(ScreenPermissions.allows("hoa-don, khach-hang", "khach-hang"));
        assertFalse(ScreenPermissions.allows("hoa-don,khach-hang", "nhan-vien"));
    }

    @Test
    void aPartialNameIsNotAMatch() {
        assertFalse(ScreenPermissions.allows("san-pham", "san-pham-chi-tiet"));
        assertFalse(ScreenPermissions.allows("hoa-don-chi-tiet", "hoa-don"));
    }

    @Test
    void noQuyenGrantsNothing() {
        assertFalse(ScreenPermissions.allows(null, "hoa-don"));
        assertFalse(ScreenPermissions.allows("", "hoa-don"));
    }

    @Test
    void anUnguardedPathIsAllowedForAnyLoggedInUser() {
        assertTrue(ScreenPermissions.allows("", null));
        assertTrue(ScreenPermissions.allows(null, null));
    }
}
