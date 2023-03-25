package com.example.gallery.controller;

import com.example.gallery.model.ImageModel;
import com.example.gallery.service.ImageGalleryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class ImageGalleryController {

    @Value("${uploadDir}")
    private String uploadFolder;

    @Autowired
    private ImageGalleryService imageGalleryService;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/")
    public String homePage(Model model) {
        List<ImageModel> listImages = imageGalleryService.getAllActiveImages();
        model.addAttribute("listImages", listImages);

        return "index";
    }

    @PostMapping("/changeName")
    public String changeName(@RequestParam("id") Long id,
                              @RequestParam("newName") String name)
    {
        imageGalleryService.changeName(id, name);
        return "redirect:/";
    }

    @GetMapping("/image/display/{id}")
    @ResponseBody
    void showImage(@PathVariable("id") Long id, HttpServletResponse response, Optional<ImageModel> imageGallery)
            throws ServletException, IOException {
        log.info("Id :: " + id);
        imageGallery = imageGalleryService.getImageById(id);
        response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
        response.getOutputStream().write(imageGallery.get().getImage());
        response.getOutputStream().close();
    }

    @GetMapping("/image/saveImage") public String displayUploadForm() {
        return "add_image_form";
    }

    @PostMapping("/image/saveImage")
    public @ResponseBody ResponseEntity<?> createProduct(Model model, HttpServletRequest request
            , final @RequestParam("image") MultipartFile file) {
        try {
            //String uploadDirectory = System.getProperty("user.dir") + uploadFolder;
            String uploadDirectory = request.getServletContext().getRealPath(uploadFolder);
            String fileName = file.getOriginalFilename();
            String filePath = Paths.get(uploadDirectory, fileName).toString();
            log.info("FileName: " + file.getOriginalFilename());
            if (fileName == null || fileName.contains("..")) {
                model.addAttribute("invalid", "Sorry! Filename contains invalid path sequence \" + fileName");
                return new ResponseEntity<>("Sorry! Filename contains invalid path sequence " + fileName, HttpStatus.BAD_REQUEST);
            }

            try {
                File dir = new File(uploadDirectory);
                if (!dir.exists()) {
                    log.info("Folder Created");
                    dir.mkdirs();
                }
                // Save the file locally
                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
                stream.write(file.getBytes());
                stream.close();
            } catch (Exception e) {
                log.info("in catch");
                e.printStackTrace();
            }
            byte[] imageData = file.getBytes();
            ImageModel imageModel = new ImageModel();
            imageModel.setName(file.getOriginalFilename());
            imageModel.setImage(imageData);
            imageModel.setSize(file.getSize());
            imageModel.setDate(new Date());
            imageGalleryService.saveImage(imageModel);
            log.info("HttpStatus===" + new ResponseEntity<>(HttpStatus.OK));
            return new ResponseEntity<>("Product Saved With File - " + fileName, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Exception: " + e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
