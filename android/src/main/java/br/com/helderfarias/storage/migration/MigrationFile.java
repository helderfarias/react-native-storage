package br.com.helderfarias.storage.migration;

import java.io.IOException;
import java.util.List;

interface MigrationFile {

    int version();

    List<Migration> migrations() throws IOException;

}
