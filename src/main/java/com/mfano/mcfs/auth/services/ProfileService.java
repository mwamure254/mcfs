package com.mfano.mcfs.auth.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mcfs.auth.models.Profile;
import com.mfano.mcfs.auth.models.User;
import com.mfano.mcfs.auth.services.UserService;
import com.mfano.mcfs.auth.repositories.ProfileRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private Profile profile,existing;
    private final ProfileRepository profileRepository;
    private final UserService userService;

     @Value("${app.upload-dir}")
    private String uploadDir;
    private static final String baseDirectory = "src/main/resources/static/images/profile/";

    private static final int PROFILE_SIZE = 512;

    // Get All Profiles
    public List<Profile> findAll() {
        return profileRepository.findAll();
    }

    // Get Profile By Id
    public Profile findById(Long id) {
        return profileRepository.findById(id).orElse(null);
    }

    // Get Profile By Id
    public Profile findByUser_Id(Long userId) {
        return profileRepository.findByUserId(userId);
    }

    // Delete Profile
    public void delete(Long id) {
        profileRepository.deleteById(id);
    }

    // Save Profile
    public void save(Profile profile) {
        profileRepository.save(profile);
    }

    // Update Profile Image
    @Transactional
    public void updateImage1(Long userid, MultipartFile file, RedirectAttributes red) throws IOException {

        existing = findByUser_Id(userid);
        try {
            byte[] resizedImage = resizeProfileImage(file);
            String filename = UUID.randomUUID() + ".jpg";
            Path path = Paths.get(baseDirectory);
            Files.createDirectories(path);
            Files.write(path.resolve(filename), resizedImage);

            existing.setImage(filename);
            save(existing);
            red.addFlashAttribute("message", "Profile image updated successsfully");
        } catch (Exception e) {
            red.addFlashAttribute("error", e.getMessage());
        }

    }
    //resize image
    public byte[] resizeProfileImage(MultipartFile file) throws IOException {

            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("Profile image is required");
            }
            if (file.getSize() > 5 * 1024 * 1024) {
                throw new IllegalArgumentException(
                    "Profile image must not exceed 5 MB"
                );
            }

            try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                Thumbnails.of(file.getInputStream())
                    .size(PROFILE_SIZE, PROFILE_SIZE)
                    .crop(Positions.CENTER)
                    .outputFormat("jpg")//webp
                    .outputQuality(0.85)
                    .toOutputStream(output);

            return output.toByteArray();
        }
    }

    // Update Profile Image
    @Transactional
    public void deleteImage1(Long userid, RedirectAttributes red) throws IOException {
        existing = findByUser_Id(userid);
        try {
            String image = existing.getImage();
            Path path = Path.of(uploadDir + "/images/profile/" + image);
            Files.delete(path);

            existing.setImage(null);
            save(existing);
            red.addFlashAttribute("message", "Profile image deleted successfully");
        } catch (Exception e) {
            red.addFlashAttribute("error", e.getMessage());
        }

    }

    // Update Profile
    public void update(Long userid, Profile profile) {
        existing = findByUser_Id(userid);

        // update only editable fields
        existing.setFin(profile.getFin());
        existing.setLan(profile.getLan());
        existing.setOther(profile.getOther());
        existing.setAbouts(profile.getAbouts());
        existing.setGender(profile.getGender());
        existing.setEmail(profile.getEmail());
        existing.setSN(profile.getSN());
        existing.setDesignation(profile.getDesignation());
        existing.setCounty(profile.getCounty());
        existing.setAddress(profile.getAddress());
        existing.setPhone(profile.getPhone());
        existing.setTwitter(profile.getTwitter());
        existing.setFacebook(profile.getFacebook());
        existing.setInstagram(profile.getInstagram());
        existing.setLinkedin(profile.getLinkedin());

        save(existing);

    }

    // check profile
    public Profile checkProfile(Long userid) {
        profile = findByUser_Id(userid);
        User user = userService.findById(userid);
        if (profile == null) {
            profile = new Profile();
            profile.setUser(user);
            save(profile);

        } 
        return profile;
    }

    ///uploads2
    @Transactional
    public void uploadImage2(Long userId, MultipartFile file, RedirectAttributes red) throws IOException {
        existing = findByUser_Id(userId);
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select an image.");
        }

        // Validate content type
        String contentType = file.getContentType();

        if (contentType == null ||
                !(contentType.equals("image/jpeg")
                        || contentType.equals("image/png")
                        || contentType.equals("image/webp"))) {

            throw new IllegalArgumentException(
                    "Only JPG, PNG and WEBP images are allowed.");
        }

        // Create profile directory
        Path profileDirectory = Paths.get(uploadDir, "images", "profile");

        Files.createDirectories(profileDirectory);

        // Get extension
        String originalName = file.getOriginalFilename();

        String extension = "";

        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(
                    originalName.lastIndexOf(".")).toLowerCase();
        }

        // Generate safe unique filename
        String filename = "user-" + userId + "-" +
                UUID.randomUUID() + extension;

        Path destination = profileDirectory.resolve(filename)
                .normalize();

        // Make sure destination remains inside upload directory
        if (!destination.startsWith(profileDirectory.normalize())) {
            throw new IOException("Invalid file path.");
        }

        Files.copy(
                file.getInputStream(),
                destination,
                StandardCopyOption.REPLACE_EXISTING);

        existing.setImage(filename);
        save(existing);
    }

    // DELETING THE IMAGE FROM ./uploads
    @Transactional
    public void deleteImage2(String filename) {

        if (filename == null || filename.isBlank()) {
            return;
        }

        try {
            Path profileDirectory = Paths
                    .get(uploadDir, "images", "profile")
                    .toAbsolutePath()
                    .normalize();

            Path image = profileDirectory
                    .resolve(filename)
                    .normalize();

            // Security: prevent ../ path traversal
            if (!image.startsWith(profileDirectory)) {
                throw new IOException("Invalid image path.");
            }

            Files.deleteIfExists(image);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to delete profile image: " + filename, e);
        }
    }

    @Transactional
    public void updateImage(Long userId, MultipartFile file,
         RedirectAttributes red) throws IOException {
        existing = findByUser_Id(userId);

        String oldImage = profile.getImage();

        // Save new image first
        uploadImage2(userId, file, red);

        // Update database
        profileRepository.save(profile);

        // Delete old image
        if (oldImage != null && !oldImage.isBlank()) {
            deleteImage2(oldImage);
        }
    }
}
