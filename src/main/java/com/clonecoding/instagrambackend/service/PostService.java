package com.clonecoding.instagrambackend.service;

import com.clonecoding.instagrambackend.domain.*;
import com.clonecoding.instagrambackend.mapper.ImageMapper;
import com.clonecoding.instagrambackend.mapper.PostMapper;
import com.clonecoding.instagrambackend.web.dto.PostDto;
import com.clonecoding.instagrambackend.web.dto.PostRequestDto;
import com.clonecoding.instagrambackend.web.dto.PostsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final FollowRepository followRepository;
    private final PostMapper postMapper;
    private final ImageMapper imageMapper;

    @Transactional
    public PostsDto getPostsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error : user is not found"));
        List<PostDto> postDtos = postRepository.findByUser(user)
                .stream()
                .map(post -> postMapper.toDto(post, postRepository))
                .collect(Collectors.toList());
        return new PostsDto(postDtos);
    }

    @Transactional
    public void createPost(PostRequestDto postRequestDto) {
        User user = userRepository.findByUsername(postRequestDto.getUsername())
                .orElseThrow(() -> new RuntimeException("Error : user is not found"));
        List<Image> images = postRequestDto.getImages()
                .stream()
                .map(imageMapper::toEntity)
                .collect(Collectors.toList());
        Post post = postMapper.toEntity(postRequestDto, user, images);
        postRepository.save(post);
        images.forEach(image -> {
            image.setPost(post);
            imageRepository.save(image);
        });
    }

    @Transactional
    public void deletePost(Long post_id) {
        Post post = postRepository.findById(post_id)
                .orElseThrow(() -> new RuntimeException("Error : post is not found"));
        postRepository.delete(post);
    }

    @Transactional
    public PostsDto getFeeds() {
        User user = getCurrentUser();
        List<Long> followingIds = followRepository.findByFollower(user)
                .stream().
                map(follow -> follow.getFollowee().getId())
                .collect(Collectors.toList());
        return new PostsDto(postRepository.findByUserIdIn(followingIds, Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(post -> postMapper.toDto(post, postRepository))
                .collect(Collectors.toList()));
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error : User is not found"));
    }
}
