package pl.marcinchwedczuk.paintme.gui;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.spi.URLStreamHandlerProvider;

// TODO: Clean this mess
// See: https://docs.oracle.com/javase%2F9%2Fdocs%2Fapi%2F%2F/java/util/ServiceLoader.html
public class ClasspathURLStreamHandlerProvider extends URLStreamHandlerProvider {

    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        System.out.println("Działa!" + protocol);

        if ("xxx".equals(protocol)) {
            return new URLStreamHandler() {
                @Override
                protected URLConnection openConnection(URL u) throws IOException {
                    return ClassLoader.getSystemClassLoader().getResource(u.getPath()).openConnection();
                }
            };
        }
        return null;
    }

}
