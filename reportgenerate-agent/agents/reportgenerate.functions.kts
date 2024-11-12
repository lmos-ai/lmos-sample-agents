// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0
import ai.ancf.lmos.arc.sample.services.PdfGenerator

function(
    name = "generateReport",
    description = "generate pdf report of product catalog",
    params = types(
        string(
            name = "json_data",
            description = "A json data having product catalog information"
        )
    ),
) { (query) ->
    val pdfGenerate = get<PdfGenerator>()
    val contextResult = query?.let { pdfGenerate.generatePdf(it, "catalog_report.pdf") }
    """
        $contextResult
    """.trimIndent()
}

