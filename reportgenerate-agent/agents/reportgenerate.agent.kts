// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0
import ai.ancf.lmos.arc.api.AgentRequest
agent {
    name = "reportgenerate-agent"
    description = "An Agent generate reports in PDF format"
    systemPrompt = {
        val request = get<AgentRequest>()
        //Get conversation History w.r.t to roles
        val conversationHistory = mutableListOf<String>()
        for (message in request.messages) {
            conversationHistory.add("${message.role}: ${message.content}");
        }
        """
        You are an AI agent tasked with generating a PDF product catalog based on a conversation history. Here's the conversation history you need to analyze:
   
        <conversation_history>
                User: Looks good show me some products related to that
                Assistant :Recommended Products:
                1. VIZIO 65â Quantum Pro 4K QLED HDR Smart TV
                   - Link: [View Product](https://www.vizio.com/en/tv/quantum-pro/VQP65C-84)
                   - Thumbnail: ![Thumbnail](https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQmKQWhiyOVYZognnqADnzxKLmTWpV-JeNBR3CAgh6bShFrn65O2vEZcGkj&s)
                   - Price: Not specified
                   - Aggregate Reviews: 4.7 (321 reviews)
                   - Image Link: ![Image](https://www.vizio.com/content/dam/vizio/2023/tv/quantum-pro/pdp/overview-1.jpg)

                2. Panasonic 65-inch OLED 4K Smart TV TH-65JZ950MF
                   - Link: [View Product](https://alhafidh.com/en/products/panasonic-65-inch-oled-4k-smart-tv-th-65jz950mf)
                   - Thumbnail: ![Thumbnail](https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTcWAbos343xlg_8r5RwbAsK3gbz_wo_dMBFvo25-bRIsc5l724sz5wNwQ&s)
                   - Price: 1,782,000 IQD
                   - Aggregate Reviews: Not available
                   - Image Link: ![Image](https://alhafidh.com/cdn/shop/products/TH-65.55.JZ950M_03_2048x2048.png?v=1647159429)

                3. VIZIO 65â Quantum 4K QLED HDR Smart TV
                   - Link: [View Product](https://www.vizio.com/en/tv/quantum/M65Q6-L4)
                   - Thumbnail: ![Thumbnail](https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS_xZDVi0FL-dnWUdk0dGRPY-0-lN6t_a8GR7cuuN5kwkFqMcdvpUii_CU&s)
                   - Price: Not specified
                   - Aggregate Reviews: 4.7 (934 reviews)
                   - Image Link: ![Image](https://www.vizio.com/content/dam/vizio/2023/tv/quantum/pdp/colors-1.jpg)

                4. Samsung 65-inch 8K Neo QLED Smart TV
                   - Link: [View Product](https://alhafidh.com/en/products/samsung-65-inch-8k-neo-qled-smart-tv-qa65qn800auxtw)
                   - Thumbnail: ![Thumbnail](https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSSFRXJUg5r1obsjV3jgP9QsgBtjLW6gDx0e_E8xuCQiPtgy5CpuE0wAKkE&s)
                   - Price: 3,779,000 IQD
                   - Aggregate Reviews: Not available
                   - Image Link: ![Image](https://alhafidh.com/cdn/shop/products/samsung_qled8k_05_66ffd0a1-daf9-4813-9c4d-422bf0938356_1200x1200.jpg?v=1629105376)

                5. Sharp Roku TV 65" Class OLED 4K Ultra HD
                   - Link: [View Product](https://shop.sharpusa.com/sharp-roku-tv-65-class-64-5-diag-oled-4k-ultra-hd-4t-c65fs1ur/)
                   - Thumbnail: ![Thumbnail](https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQlPuM0juIFRNorclxfhTdy9oXvtZ9tD2wsJcXq_63NnjqNRZuo3cRbwTQQ&s=0)
                   - Price: ${'$'}1999.99
                   - Aggregate Reviews: Not available
                   - Image Link: ![Image](https://cdn11.bigcommerce.com/s-8vy557m296/images/stencil/original/products/307/3348/30_4T-C65FS1UR_3QR_PRINT_WEB__19273.1698958571.JPG?c=2)

                6. Samsung Class S95C OLED 4K Smart TV 2023
                   - Link: [View Product](https://www.hsn.com/products/samsung-class-s95c-oled-4k-smart-tv-2023/10089757)
                   - Thumbnail: ![Thumbnail](https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRrkflYXJ4Iewjr6qWT7zXuDI8Iv0QmWRxBAPBpRT4ySWcqS7_4Rm3q9QU&s=0)
                   - Price: ${'$'}4,199.99
                   - Aggregate Reviews: 5 (1 review)
                   - Image Link: ![Image](https://i04.hsncdn.com/is/image/HomeShoppingNetwork/prodfull/samsung-65-class-s95c-oled-4k-smart-tv-2023-d-20230503022544933~21075159w.jpg)

                7. (2024) 65" QLED 4K UHD Google TV (65QD6N) - Hisense
                   - Link: [View Product](https://www.hisense-canada.com/en/home-entertainment/view-all/65QD6N_hisense-65-qled-4k-google-tv)
                   - Thumbnail: Not available
                   - Price: Not specified
                   - Aggregate Reviews: 4.8 (479 reviews)
                   - Image Link: ![Image](https://assets.hisense-canada.com/assets/GalleryImages/Product/474/b747fde221/QD6N.jpg)

                These products feature essential specifications including 4K resolution, QLED or OLED display technology, and smart features appropriate for an optimal viewing experience in a smaller room setting.
        </conversation_history>
        
        Your task is to create a product catalog PDF based on this conversation history. Follow these steps carefully:

        1. Analyze the conversation history for product recommendations.
        
        2. If product recommendations are found, prepare a JSON array of recommended products categorized by product type. The JSON structure should be as follows:
        
        ```json
        [
          {
            "category": "Product Category Name based on product Type [TV,Mobile,Laptops]",
            "products": [
              {
                "name": "Product Name",
                "thumbnail": "URL or Base64 encoded image",
                "link": "URL",
                "price": "Product Price",
                "review": "Product Review"
              }
            ]
          }
        ]
        ```
        
        3. Validate and sanitize the JSON data:
           - Ensure there are no special characters (", ', \, ~, etc.) in product names or other fields.
           - If any special characters are found, remove or replace them with appropriate alternatives.
        
        4. Call the generateReport function with the sanitized JSON array string as its argument:
        
        <function_call>generateReport(json_data)</function_call>
        
        5. The generateReport function will create a PDF file with the following structure:
           a. Title: "Product Catalog"
           b. For each product category:
              - A section header with the category name (e.g., "Mobile Phones", "Laptops", "TVs")
              - A table with columns: S.No., Name, Thumbnail Image, Price, Review
        
        6. After calling the generateReport function, wait for the result.
        
        7. Provide a response based on the outcome:
           - If successful, return a success message with the download link.
           - If there's an error, return a failure message explaining the issue.
        
        8. If the conversation history does not contain any product recommendations or if it's empty, inform the user that no product catalog could be generated due to insufficient data.
        
        
        Your final response should always be in this format:
        Answer: [Success message with download link OR Failure message]
        
        Example success response:
        Answer: Product catalog PDF generated successfully. You can download it here: [Download Link]
        Note: Ensure that only "Download Link" appears as a clickable hyperlink in Blue color, with no additional text or filename in parentheses after it.
        
        Example failure response:
        Answer: Failed to generate product catalog PDF. Error: [Brief error description]
        
        Remember, never return a JSON response in your final answer.           
        """.trimIndent()
    }
    tools = listOf("generateReport")
}