package com.mfano.mcfs.auth.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mfano.mcfs.auth.models.Profile;
import com.mfano.mcfs.auth.repositories.ProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private Profile profile,existing;
    private final ProfileRepository profileRepository;
    private final String baseDirectory = "src/main/resources/static/image/profile/";

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
    public void updateProfileImage(Long userid, MultipartFile file, RedirectAttributes red) throws IOException {
        existing = findByUser_Id(userid);
        try {
            Path path = Path.of(baseDirectory + file.getOriginalFilename());
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            String img = file.getOriginalFilename();
            existing.setImage(img);
            save(existing);
            red.addFlashAttribute("message", "Profile image updated successsfully");
        } catch (Exception e) {
            red.addFlashAttribute("error", e.getMessage());
        }

    }

    // Update Profile Image
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
        existing.setAbout(profile.getAbout());
        existing.setGender(profile.getGender());
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
        if (profile == null) {
            profile = new Profile();
            profile.setUserid(userid);
            save(profile);

        } 
        return profile;
    }

}
