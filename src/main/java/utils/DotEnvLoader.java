/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import io.github.cdimascio.dotenv.Dotenv;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author Victor Okonkwo
 */
public class DotEnvLoader {
    final static Path path = Paths.get(".");
    final static Dotenv dotenv = Dotenv.configure()
            .directory(path.toString())
            .ignoreIfMalformed()
            .ignoreIfMissing()
            .load();

    public static Dotenv getDotenv() {
        return dotenv;
    }

}
