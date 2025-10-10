/*
 *  Licensed to GraphHopper GmbH under one or more contributor
 *  license agreements. See the NOTICE file distributed with this work for
 *  additional information regarding copyright ownership.
 *
 *  GraphHopper GmbH licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except in
 *  compliance with the License. You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.graphhopper.util;

import com.graphhopper.coll.GHIntLongHashMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Peter Karich
 */
public class GHUtilityTest {

    @Test
    public void testEdgeStuff() {
        assertEquals(2, GHUtility.createEdgeKey(1, false));
        assertEquals(3, GHUtility.createEdgeKey(1, true));
    }

    @Test
    public void testZeroValue() {
        GHIntLongHashMap map1 = new GHIntLongHashMap();
        assertFalse(map1.containsKey(0));
        // assertFalse(map1.containsValue(0));
        map1.put(0, 3);
        map1.put(1, 0);
        map1.put(2, 1);

        // assertTrue(map1.containsValue(0));
        assertEquals(3, map1.get(0));
        assertEquals(0, map1.get(1));
        assertEquals(1, map1.get(2));

        // instead of assertEquals(-1, map1.get(3)); with hppc we have to check before:
        assertTrue(map1.containsKey(0));

        // trove4j behaviour was to return -1 if non existing:
//        TIntLongHashMap map2 = new TIntLongHashMap(100, 0.7f, -1, -1);
//        assertFalse(map2.containsKey(0));
//        assertFalse(map2.containsValue(0));
//        map2.add(0, 3);
//        map2.add(1, 0);
//        map2.add(2, 1);
//        assertTrue(map2.containsKey(0));
//        assertTrue(map2.containsValue(0));
//        assertEquals(3, map2.get(0));
//        assertEquals(0, map2.get(1));
//        assertEquals(1, map2.get(2));
//        assertEquals(-1, map2.get(3));
    }

        @org.junit.jupiter.api.Test
    public void testEdgeKeyRoundtrip() {
        int edgeId = 7;
        int keyTrue = GHUtility.createEdgeKey(edgeId, true);
        int keyFalse = GHUtility.createEdgeKey(edgeId, false);
        org.junit.jupiter.api.Assertions.assertEquals(edgeId, GHUtility.getEdgeFromEdgeKey(keyTrue));
        org.junit.jupiter.api.Assertions.assertEquals(edgeId, GHUtility.getEdgeFromEdgeKey(keyFalse));
        org.junit.jupiter.api.Assertions.assertEquals(keyTrue, GHUtility.reverseEdgeKey(GHUtility.reverseEdgeKey(keyTrue)));
        org.junit.jupiter.api.Assertions.assertEquals(1, Math.abs(keyTrue - keyFalse));
    }

    @org.junit.jupiter.api.Test
    public void testAsSet() {
        java.util.Set<Integer> s = GHUtility.asSet(1, 2, 2, 3, 1);
        org.junit.jupiter.api.Assertions.assertEquals(3, s.size());
        org.junit.jupiter.api.Assertions.assertTrue(s.contains(1));
        org.junit.jupiter.api.Assertions.assertTrue(s.contains(2));
        org.junit.jupiter.api.Assertions.assertTrue(s.contains(3));
    }

    @org.junit.jupiter.api.Test
    public void testRandomDoubleInRange_deterministic_and_bounds() {
        java.util.Random rnd1 = new java.util.Random(12345L);
        java.util.Random rnd2 = new java.util.Random(12345L);
        double min = 5.0;
        double max = 10.0;
        double v1 = GHUtility.randomDoubleInRange(rnd1, min, max);
        double v2 = GHUtility.randomDoubleInRange(rnd2, min, max);
        org.junit.jupiter.api.Assertions.assertEquals(v1, v2, 1e-12);
        org.junit.jupiter.api.Assertions.assertTrue(v1 >= min && v1 <= max);
    }

    @org.junit.jupiter.api.Test
    public void testRandomDoubleInRange_equalMinMax() {
        java.util.Random rnd = new java.util.Random(0L);
        double fixed = 2.71828;
        double v = GHUtility.randomDoubleInRange(rnd, fixed, fixed);
        org.junit.jupiter.api.Assertions.assertEquals(fixed, v, 0.0);
    }

    @org.junit.jupiter.api.Test
    public void testCreateRectangleAndCircle() {
        com.graphhopper.util.JsonFeature rect = GHUtility.createRectangle("rect-id", 1.0, 2.0, 3.0, 4.0);
        org.junit.jupiter.api.Assertions.assertEquals("rect-id", rect.getId());
        org.junit.jupiter.api.Assertions.assertNotNull(rect.getGeometry());
        org.junit.jupiter.api.Assertions.assertTrue(rect.getGeometry() instanceof org.locationtech.jts.geom.Polygon);
        org.locationtech.jts.geom.Polygon rp = (org.locationtech.jts.geom.Polygon) rect.getGeometry();
        org.junit.jupiter.api.Assertions.assertEquals(5, rp.getCoordinates().length);

        com.graphhopper.util.JsonFeature circ = GHUtility.createCircle("circ-id", 1.0, 2.0, 1000.0);
        org.junit.jupiter.api.Assertions.assertEquals("circ-id", circ.getId());
        org.junit.jupiter.api.Assertions.assertNotNull(circ.getGeometry());
        org.junit.jupiter.api.Assertions.assertTrue(circ.getGeometry() instanceof org.locationtech.jts.geom.Polygon);
        org.locationtech.jts.geom.Polygon cp = (org.locationtech.jts.geom.Polygon) circ.getGeometry();
        org.junit.jupiter.api.Assertions.assertEquals(37, cp.getCoordinates().length);
    }

    @org.junit.jupiter.api.Test
    public void testRunConcurrently() {
        java.util.concurrent.atomic.AtomicInteger ai = new java.util.concurrent.atomic.AtomicInteger(0);
        java.util.stream.Stream<Runnable> runnables = java.util.stream.Stream.of(
                () -> ai.addAndGet(1),
                () -> ai.addAndGet(2),
                () -> ai.addAndGet(3),
                () -> ai.addAndGet(4)
        );
        GHUtility.runConcurrently(runnables, 2);
        org.junit.jupiter.api.Assertions.assertEquals(10, ai.get());
    }

        @org.junit.jupiter.api.Test
    public void testManyEdgeKeysUniqueAndRecoverable() {
        final int N = 500;
        java.util.Set<Integer> keys = new java.util.HashSet<>(N);
        for (int i = 0; i < N; i++) {
            int id = i * 2 + 1; // ids non nuls et variés
            int k1 = GHUtility.createEdgeKey(id, false);
            int k2 = GHUtility.createEdgeKey(id, true);
            // clé récupère l'id
            org.junit.jupiter.api.Assertions.assertEquals(id, GHUtility.getEdgeFromEdgeKey(k1));
            org.junit.jupiter.api.Assertions.assertEquals(id, GHUtility.getEdgeFromEdgeKey(k2));
            keys.add(k1);
            keys.add(k2);
        }
        // chaque paire (id,false) et (id,true) doit produire des clés distinctes
        org.junit.jupiter.api.Assertions.assertEquals(N * 2, keys.size());
    }
}
