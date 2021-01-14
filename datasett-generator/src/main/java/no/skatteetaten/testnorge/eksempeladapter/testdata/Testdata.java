package no.skatteetaten.testnorge.eksempeladapter.testdata;

import java.util.Random;

public class Testdata {

    private final String id;
    private final String pid;
    private final boolean felt = new Random().nextBoolean();

    public Testdata(String id, String pid) {
        this.id = id;
        this.pid = pid;
    }

    public String getId() {
        return id;
    }

    public String getPid() {
        return pid;
    }

    public boolean isFelt() {
        return felt;
    }
}
