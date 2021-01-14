package no.skatteetaten.testnorge.eksempeladapter;

import no.skatteetaten.testnorge.eksempeladapter.command.CommandRunner;
import no.skatteetaten.testnorge.eksempeladapter.command.ErstattTestdataRunner;
import no.skatteetaten.testnorge.eksempeladapter.command.SendKonfigurationRunner;
import no.skatteetaten.testnorge.eksempeladapter.command.SendTestdataRunner;

public enum Commands {
    CONFIG(new SendKonfigurationRunner()),
    TESTDATA(new SendTestdataRunner()),
    ERSTATT(new ErstattTestdataRunner());

    private CommandRunner runner;

    Commands(CommandRunner runner) {
        this.runner = runner;
    }

    public CommandRunner getRunner() {
        return runner;
    }
}
