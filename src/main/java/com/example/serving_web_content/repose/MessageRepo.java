package com.example.serving_web_content.repose;

import com.example.serving_web_content.Domain.Message;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepo extends CrudRepository<Message, Integer> {

}
