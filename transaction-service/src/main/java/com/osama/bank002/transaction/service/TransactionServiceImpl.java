package com.osama.bank002.transaction.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.osama.bank002.transaction.client.AccountClient;
import com.osama.bank002.transaction.domain.dto.LogTransactionRequest;
import com.osama.bank002.transaction.domain.dto.ProfileSummary;
import com.osama.bank002.transaction.domain.dto.TransactionDto;
import com.osama.bank002.transaction.domain.entity.LedgerEntry;
import com.osama.bank002.transaction.mapper.TxMapper;
import com.osama.bank002.transaction.repository.LedgerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final LedgerRepository repo;
    private final AccountClient accountClient;
    private final EmailService emailService;


    /**
     * retrive list of transaction within a date range given as account number
     * generate a pdf file
     * send the file via email
     */

    @Override
    @Transactional
    public TransactionDto log(LogTransactionRequest req) {
        LedgerEntry e = LedgerEntry.builder()
                .id(UUID.randomUUID().toString())
                .accountNumber(req.accountNumber())
                .transactionType(req.transactionType().toUpperCase())
                .amount(req.amount().setScale(2, RoundingMode.HALF_UP))
                .status(req.status().toUpperCase())
                .createdAt(req.createdAt() != null ? req.createdAt() : LocalDateTime.now())
                .build();
        repo.save(e);
        return TxMapper.toDto(e);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionDto> list(String accountNumber, String from, String to) {
        LocalDateTime f = LocalDate.parse(from).atStartOfDay();
        LocalDateTime t = LocalDate.parse(to).atTime(LocalTime.MAX);
        return repo.findRange(accountNumber, f, t).stream().map(TxMapper::toDto).toList();
    }

    /**
     * Builds a PDF, returns it as bytes, and (optionally) emails it to the account owner.
     */
    @Override
    public byte[] statementPdf(String accountNumber, String from, String to, boolean email) {
        LocalDate start = LocalDate.parse(from);
        LocalDate end = LocalDate.parse(to);
        LocalDateTime f = start.atStartOfDay();
        LocalDateTime t = end.atTime(LocalTime.MAX);

        // ask account-service for owner info (fullName, email)
        ProfileSummary owner = accountClient.owner(accountNumber);
        String customerName = owner != null ? owner.fullName() : "Customer";
        String emailAddr = owner != null ? owner.email() : null;

        List<LedgerEntry> txs = repo.findRange(accountNumber, f, t);

        try (var baos = new ByteArrayOutputStream()) {
            var doc = new com.lowagie.text.Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            var titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.WHITE);
            var headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
            var subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            var normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            var boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

            // bank header
            PdfPTable bankInfo = new PdfPTable(1);
            bankInfo.setWidthPercentage(100);
            bankInfo.setSpacingAfter(20f);
            PdfPCell bankName = new PdfPCell(new Phrase("001 BANK", titleFont));
            bankName.setBorder(0);
            bankName.setBackgroundColor(new Color(0, 51, 102));
            bankName.setPadding(15f);
            bankName.setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell bankAddr = new PdfPCell(new Phrase("Riyadh, Kingdom of Saudi Arabia", normalFont));
            bankAddr.setBorder(0);
            bankAddr.setBackgroundColor(new Color(240, 248, 255));
            bankAddr.setPadding(10f);
            bankAddr.setHorizontalAlignment(Element.ALIGN_CENTER);
            bankInfo.addCell(bankName);
            bankInfo.addCell(bankAddr);

            // title
            PdfPTable title = new PdfPTable(1);
            title.setWidthPercentage(100);
            title.setSpacingAfter(15f);
            PdfPCell tcell = new PdfPCell(new Phrase("STATEMENT OF ACCOUNT", subHeaderFont));
            tcell.setBorder(0);
            tcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tcell.setPadding(10f);
            title.addCell(tcell);

            // info
            PdfPTable info = new PdfPTable(2);
            info.setWidthPercentage(100);
            info.setSpacingAfter(20f);
            info.setWidths(new float[]{1f, 1f});

            PdfPCell left = new PdfPCell();
            left.setBorder(Rectangle.BOX);
            left.setPadding(15f);
            Paragraph lp = new Paragraph();
            lp.add(new Chunk("CUSTOMER INFORMATION\n", boldFont));
            lp.add(new Chunk("Name: " + customerName + "\n", normalFont));
            lp.add(new Chunk("Account Number: " + accountNumber + "\n", normalFont));
            info.addCell(left);
            left.addElement(lp);

            PdfPCell right = new PdfPCell();
            right.setBorder(Rectangle.BOX);
            right.setPadding(15f);
            Paragraph rp = new Paragraph();
            rp.add(new Chunk("STATEMENT PERIOD\n", boldFont));
            rp.add(new Chunk("Start Date: " + start + "\n", normalFont));
            rp.add(new Chunk("End Date: " + end + "\n", normalFont));
            rp.add(new Chunk("Generated: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), normalFont));
            right.addElement(rp);
            info.addCell(right);

            // table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2f, 3f, 2.5f, 1.5f});
            table.setHeaderRows(1);
            for (String h : List.of("DATE", "TRANSACTION TYPE", "AMOUNT", "STATUS")) {
                PdfPCell hc = new PdfPCell(new Phrase(h, headerFont));
                hc.setBackgroundColor(new Color(0, 51, 102));
                hc.setBorder(Rectangle.BOX);
                hc.setPadding(12f);
                hc.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(hc);
            }

            BigDecimal totalDebit = BigDecimal.ZERO, totalCredit = BigDecimal.ZERO;
            boolean even = true;
            for (var tx : txs) {
                Color row = even ? Color.WHITE : new Color(248, 249, 250);
                even = !even;

                String when = tx.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy\nHH:mm:ss"));
                var dateCell = new PdfPCell(new Phrase(when, normalFont));
                dateCell.setBackgroundColor(row);
                dateCell.setPadding(8f);
                dateCell.setHorizontalAlignment(Element.ALIGN_CENTER);

                var typeCell = new PdfPCell(new Phrase(tx.getTransactionType(), normalFont));
                typeCell.setBackgroundColor(row);
                typeCell.setPadding(8f);

                var amountStr = String.format("%.2f SAR", tx.getAmount().doubleValue());
                var amountCell = new PdfPCell(new Phrase(amountStr, boldFont));
                amountCell.setBackgroundColor(row);
                amountCell.setPadding(8f);
                amountCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

                var statusCell = new PdfPCell(new Phrase(tx.getStatus(), boldFont));
                statusCell.setBackgroundColor(row);
                statusCell.setPadding(8f);
                statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);

                table.addCell(dateCell);
                table.addCell(typeCell);
                table.addCell(amountCell);
                table.addCell(statusCell);

                if ("DEBIT".equalsIgnoreCase(tx.getTransactionType())) totalDebit = totalDebit.add(tx.getAmount());
                if ("CREDIT".equalsIgnoreCase(tx.getTransactionType())) totalCredit = totalCredit.add(tx.getAmount());
            }

            // summary
            PdfPTable summary = new PdfPTable(2);
            summary.setWidthPercentage(100);
            summary.setSpacingBefore(20f);
            summary.setWidths(new float[]{3f, 1f});
            summary.addCell(cellR("Total Transactions:", boldFont));
            summary.addCell(cellR(String.valueOf(txs.size()), boldFont));
            summary.addCell(cellR("Total Credits:", boldFont));
            summary.addCell(cellR(String.format("%.2f SAR", totalCredit.doubleValue()), boldFont));
            summary.addCell(cellR("Total Debits:", boldFont));
            summary.addCell(cellR(String.format("%.2f SAR", totalDebit.doubleValue()), boldFont));
            summary.addCell(topBorder(cellR("Net Balance:", boldFont)));
            BigDecimal net = totalCredit.subtract(totalDebit);
            summary.addCell(topBorder(cellR(String.format("%.2f SAR", net.doubleValue()), boldFont)));

            // footer
            PdfPTable footer = new PdfPTable(1);
            footer.setWidthPercentage(100);
            footer.setSpacingBefore(30f);
            PdfPCell fc = new PdfPCell(new Phrase(
                    "This statement is computer generated and does not require a signature.\nFor any queries, please contact our customer service.",
                    FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY)
            ));
            fc.setBorder(0);
            fc.setHorizontalAlignment(Element.ALIGN_CENTER);
            fc.setPadding(10f);
            footer.addCell(fc);

            doc.add(bankInfo);
            doc.add(title);
            doc.add(info);
            doc.add(table);
            doc.add(summary);
            doc.add(footer);
            doc.close();

            byte[] pdf = baos.toByteArray();

            if (email && emailAddr != null && !emailAddr.isBlank()) {
                // write temp file and email (simplest)
                File tmp = File.createTempFile("statement-", ".pdf");
                try (FileOutputStream fos = new FileOutputStream(tmp)) {
                    fos.write(pdf);
                }
                emailService.sendEmailWithAttachment(emailAddr, "STATEMENT OF ACCOUNT",
                        "Kindly find your requested account statement attached!", tmp);
                tmp.deleteOnExit();
            }
            return pdf;

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate statement", e);
        }
    }

    private PdfPCell cellR(String text, Font f) {
        PdfPCell c = new PdfPCell(new Phrase(text, f));
        c.setBorder(0);
        c.setPadding(5f);
        c.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return c;
    }

    private PdfPCell topBorder(PdfPCell c) {
        c.setBorder(Rectangle.TOP);
        c.setBorderColor(Color.BLACK);
        c.setPaddingTop(8f);
        return c;
    }
}