package com.alfredorueda.portfolio.domain.exception;

/**
 * EntityNotFoundException is a base class for exceptions related to missing entities.
 * 
 * In DDD terms, this represents a more general case of an identity-related domain rule violation.
 * Specific types of entity not found exceptions (like PortfolioNotFoundException)
 * extend this class to provide more specific context.
 * 
 * This exception is typically thrown when:
 * - An operation references an entity by ID that doesn't exist
 * - An expected relationship between entities cannot be found
 * 
 * Using this hierarchy allows for more granular exception handling while still
 * maintaining the ability to catch a broader category of "not found" errors.
 */
public class EntityNotFoundException extends DomainException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
