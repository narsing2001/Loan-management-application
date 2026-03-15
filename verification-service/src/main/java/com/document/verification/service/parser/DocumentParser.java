package com.document.verification.service.parser;

import com.document.verification.service.dto.ParsedDocumentDTO;

public interface DocumentParser {

    boolean supports(String documentType);

    ParsedDocumentDTO parse(String text);
}
