package edu.yu.cs.com1320.project.stage5.impl;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage5.impl.DocumentPersistenceManager;
import org.junit.jupiter.api.Test;

import edu.yu.cs.com1320.project.stage5.Document;


class Serialize {


    URI uri = URI.create("http://www.yu.edu");

    DocumentImpl val = new DocumentImpl(uri, "hello, goodbye".getBytes());

    DocumentPersistenceManager dpm = new DocumentPersistenceManager(null);

    @Test
    public void test() throws IOException {
        dpm.serialize(uri, val);
        DocumentImpl val1 = (DocumentImpl) dpm.deserialize(uri);
        assertEquals(val1.hashCode(), val.hashCode());
        assertTrue(val1.equals(val));
    }

}