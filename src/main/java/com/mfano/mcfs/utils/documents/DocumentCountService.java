package com.mfano.mcfs.utils.documents;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mfano.mcfs.dtos.DocumentCount;
import com.mfano.mcfs.records.models.DocumentType;
import com.mfano.mcfs.records.services.DocumentService;
import com.mfano.mcfs.records.services.DocumentTypeService;

@Service
public class DocumentCountService {
    private final DocumentTypeService typeService;
    private final DocumentService docService;

    public DocumentCountService(DocumentTypeService typeService, DocumentService docService) {
        this.typeService = typeService;
        this.docService = docService;
    }

    public List<DocumentCount> getDocumentProgress() {
        List<DocumentType> types = typeService.findAll();
        long total = docService.count();
        List<DocumentCount> result = new ArrayList<>();

        for (DocumentType type : types) {
            long count = docService.countByType(type);
            double percentage = total == 0
                    ? 0
                    : ((double) count / total) * 100;

            String color;

            if (percentage < 25) {
                color = "bg-danger";
            } else if (percentage < 50) {
                color = "bg-warning";
            } else if (percentage < 75) {
                color = "";
            } else {
                color = "bg-info";
            }

            result.add(new DocumentCount(
                    type.getName(),
                    count,
                    Math.round(percentage * 100.0) / 100.0,
                    color));
        }

        return result;
    }
}
