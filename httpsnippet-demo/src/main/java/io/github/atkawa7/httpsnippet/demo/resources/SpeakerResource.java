package io.github.atkawa7.httpsnippet.demo.resources;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.*;

import io.github.atkawa7.httpsnippet.demo.dto.SpeakerDTO;
import io.github.atkawa7.httpsnippet.demo.processors.SpeakerProcessor;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/speakers")
@Api(tags = "Speakers")
public class SpeakerResource {

  private final SpeakerProcessor speakerProcessor;

  public SpeakerResource(SpeakerProcessor speakerProcessor) {
    this.speakerProcessor = speakerProcessor;
  }

  @GetMapping
  public HttpEntity<Page<SpeakerDTO>> listSpeakers(Pageable pageable) {
    return speakerProcessor.listSpeakers(pageable);
  }

  @PostMapping
  public HttpEntity<SpeakerDTO> createSpeaker(@RequestBody SpeakerDTO dto) {
    return speakerProcessor.createSpeaker(dto);
  }

  @PutMapping("/{id}")
  public HttpEntity<SpeakerDTO> putSpeaker(@PathVariable Long id, @RequestBody SpeakerDTO dto) {
    return speakerProcessor.updateSpeaker(id, dto);
  }

  @PatchMapping("/{id}")
  public HttpEntity<SpeakerDTO> patchSpeaker(@PathVariable Long id, @RequestBody SpeakerDTO dto) {
    return speakerProcessor.updateSpeaker(id, dto);
  }

  @GetMapping("/{id}")
  public HttpEntity<SpeakerDTO> retrieveSpeaker(@PathVariable Long id) {
    return speakerProcessor.retrieveSpeaker(id);
  }

  @DeleteMapping("/{id}")
  public HttpEntity<SpeakerDTO> deleteSpeaker(@PathVariable Long id) {
    return speakerProcessor.deleteSpeaker(id);
  }
}
