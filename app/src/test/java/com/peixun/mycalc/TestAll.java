package com.peixun.mycalc;

import junit.framework.TestCase;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by simbaba on 2018/2/25.
 * for peixun
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CalcLogicTest.class,
        CalcSettingsTest.class,
})
public class TestAll extends TestCase {
}
