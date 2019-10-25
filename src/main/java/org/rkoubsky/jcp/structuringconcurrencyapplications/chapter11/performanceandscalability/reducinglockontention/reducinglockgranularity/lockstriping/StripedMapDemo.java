package org.rkoubsky.jcp.structuringconcurrencyapplications.chapter11.performanceandscalability.reducinglockontention.reducinglockgranularity.lockstriping;

public class StripedMapDemo {
    public static void main(final String[] args) {
        final StripedMap map = new StripedMap(1);

        final String mykey1 = "mykey1";
        final String myvalue1 = "myvalue1";
        final String mykey2 = "mykey2";

        map.put(mykey1, myvalue1);
        map.put(mykey2, "myvalue2");

        System.out.println("mykey1 = " + map.get(mykey1));
        System.out.println("mykey2 = " + map.get(mykey2));

        map.put(mykey1, "updatevalue1");
        System.out.println("mykey1 = " + map.get(mykey1));
    }
}
