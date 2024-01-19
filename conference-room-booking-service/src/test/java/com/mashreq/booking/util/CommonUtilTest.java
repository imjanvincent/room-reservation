package com.mashreq.booking.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author janv@mashreq.com
 */
class CommonUtilTest {

    @Test
    void testBuildSuccessResponse() {
        CommonUtil.buildSuccessResponse("Test");
        Assertions.assertNotNull(CommonUtil.buildSuccessResponse("Test"));
    }
}