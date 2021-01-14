package no.skatteetaten.testnorge.eksempeladapter;

import java.util.Arrays;

import no.skatteetaten.testnorge.tenor.ApiException;

public class Application {

    public static void main(String[] args) throws Exception {
        System.out.println("Args: " + Arrays.toString(args));
        if (args.length > 1) {
            try {
                Commands.valueOf(args[0]).getRunner().run(args);
            } catch (ApiException e) {
                System.err.println("En feil skjedde.");
                System.err.println(e.getCode());
                System.err.println(e.getResponseHeaders());
                System.err.println(e.getResponseBody());
                e.printStackTrace();

            }
            return;
        }

        for (Commands c : Commands.values()) {
            System.out.println("Kommando: " + c.name());
            System.out.println("Argumenter:");
            c.getRunner().usage();
        }
    }
}
