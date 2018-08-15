package co.uk.netifi.ocbu;

import co.uk.netifi.ocbu.models.Product;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class ImageExtraction {

    public void copy(List<Product> products, String fromDirectory, String toDirectory) {
        for (Product product :  products) {
            String imageFileName = product.getImageName() + "." + product.getImageExt();
            Path fromPath = Paths.get(fromDirectory + "\\" + imageFileName);
            Path toPath = Paths.get(toDirectory + "\\" + imageFileName);
            if (Files.exists(fromPath)) {
                try {
                    Files.copy(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Missing File : " + fromPath);
                Path projectRootPath = Paths.get(".").toAbsolutePath().normalize();
                Path noImagePath = projectRootPath.resolve("no_image.png");
                System.out.println(noImagePath);
                System.out.println(Files.exists(noImagePath));

                try {
                    Files.copy(noImagePath, toPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
