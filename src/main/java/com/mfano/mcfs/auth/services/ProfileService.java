package com.mfano.mcfs.auth.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

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
    public void updateProfileImage(Long userid, MultipartFile file, RedirectAttributes red) throws IOException {

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
    public void deleteProfileImage(Long userid, RedirectAttributes red) throws IOException {
        existing = findByUser_Id(userid);
        try {
            String image = existing.getImage();
            Path path = Path.of(baseDirectory + image);
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

}
