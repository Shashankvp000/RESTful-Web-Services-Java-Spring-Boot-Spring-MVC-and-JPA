package com.udemy.uone.sharedutils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UtilsTest{

    @Autowired
    Utils utils;

    @BeforeEach
    void setUp() throws Exception{
    }

    @Test
    final void testgenerateUserId() {
        String userId1 = utils.generateUserId(30);
        String userId2 = utils.generateUserId(30);

        assertNotNull(userId1);
        assertNotNull(userId2);

        assertTrue(userId1.length() == 30);
        assertTrue(!userId1.equalsIgnoreCase(userId2));
    }

}