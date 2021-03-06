package com.transistorwebservices.libraryapi.publisher;

import com.transistorwebservices.libraryapi.exception.LibraryResourceAlreadyExistException;
import com.transistorwebservices.libraryapi.exception.LibraryResourceNotFoundException;
import com.transistorwebservices.libraryapi.util.LibraryApiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PublisherService {

    private static Logger logger = LoggerFactory.getLogger(PublisherService.class);

    private PublisherRepository publisherRepository;

    public PublisherService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    public Publisher getPublisher(Integer publisherId, String traceId) throws LibraryResourceNotFoundException {

        Optional<PublisherEntity> publisherEntity = publisherRepository.findById(publisherId);
        Publisher publisher = null;

        if (publisherEntity.isPresent()) {
            PublisherEntity pe = publisherEntity.get();
            publisher = createPublisherFromEntity(pe);
        } else {
            throw new LibraryResourceNotFoundException(traceId, " Publisher Id: " + publisherId + " Not Found ");
        }
        return publisher;
    }

    private Publisher createPublisherFromEntity(PublisherEntity pe) {
        return new Publisher(pe.getPublisherId(), pe.getName(), pe.getEmailId(), pe.getPhoneNumber());
    }

    public void addPublisher(Publisher publisherToBeAdded, String traceId)
            throws LibraryResourceAlreadyExistException {
        logger.debug("TraceId: {}, Request to add publisher: {}", traceId, publisherToBeAdded);
        PublisherEntity publisherEntity = new PublisherEntity(
                publisherToBeAdded.getName(),
                publisherToBeAdded.getEmailId(),
                publisherToBeAdded.getPhoneNumber()
        );

        PublisherEntity addedPublisher = null;

        try {
            addedPublisher = publisherRepository.save(publisherEntity);
        } catch (DataIntegrityViolationException e) {
            logger.error("TraceId: {}", "Publisher already exists!!", traceId, e);
            throw new LibraryResourceAlreadyExistException(traceId, "Publisher Already Exist!!");
        }
        publisherToBeAdded.setPublisherId(addedPublisher.getPublisherId());
        logger.info("TraceId: {}", "Publisher added : {}", traceId, publisherToBeAdded);
    }

    public void updatePublisher(Publisher publisherToBeUpdated, String traceId)
            throws LibraryResourceNotFoundException {
        Optional<PublisherEntity> publisherEntity = publisherRepository.findById(publisherToBeUpdated.getPublisherId());
        Publisher publisher = null;

        if (!publisherEntity.isPresent()) {
            PublisherEntity pe = publisherEntity.get();
            if (LibraryApiUtils.doesStringValueExist(publisherToBeUpdated.getEmailId())) {
                pe.setEmailId(publisherToBeUpdated.getEmailId());
            }
            if (LibraryApiUtils.doesStringValueExist(publisherToBeUpdated.getPhoneNumber())) {
                pe.setPhoneNumber(publisherToBeUpdated.getPhoneNumber());
            }
            publisherRepository.save(pe);
            publisherToBeUpdated = createPublisherFromEntity(pe);
        } else {
            throw new LibraryResourceNotFoundException(traceId, "Publisher Id :" + publisherToBeUpdated.getPublisherId() + " Not found ");

        }
    }
}
