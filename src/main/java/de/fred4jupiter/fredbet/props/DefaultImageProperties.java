package de.fred4jupiter.fredbet.props;

import org.springframework.core.io.Resource;

public record DefaultImageProperties(Resource profileImageResource, Resource thumbProfileImageResource) {
}
