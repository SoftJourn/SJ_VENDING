package com.softjourn.vending.service;

import com.softjourn.vending.dao.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService {

  private final ImageRepository repository;
}
