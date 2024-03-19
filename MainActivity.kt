package com.microsoft.calendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import io.ktor.client.*
import io.ktor.client.request.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.util.regex.Pattern
import io.ktor.client.statement.*
import kotlin.system.exitProcess
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.net.URL
import java.net.HttpURLConnection
import java.io.BufferedReader
import java.io.InputStreamReader
import org.json.JSONObject



private suspend fun fetchData(): String {
    val client = HttpClient()
    val response: HttpResponse = client.get("https://www.stocktitan.net/news/live.html")
    val body: String = response.bodyAsText()
    val doc: Document = Jsoup.parse(body)

    val divs = doc.select("div")

    val printedTexts = mutableSetOf<String>()
    val printedHrefs = mutableSetOf<String>()

    val anahtarKelimeListeleri = listOf(
        listOf("Announces", "Contract"),
        listOf("Announces", "Contracts"),
        listOf("Strategic", "Contract"),
        listOf("Strategic", "Contracts"),
        listOf("Government", "Contract"),
        listOf("US", "Contract"),
        listOf("USA", "Contract"),
        listOf("UK", "Contract"),
        listOf("FDA", "Approves"),
        listOf("FDA", "Approved"),
        listOf("NDA", "Approval"),
        listOf("CMS", "Approval"),
        listOf("CMS", "Approves"),
        listOf("CMS", "Approved"),
        listOf("CMS", "Accept"),
        listOf("CMS", "Approves"),
        listOf("CMS", "Authority"),
        listOf("CMS", "Authorization"),
        listOf("CMS", "Support"),
        listOf("CMS", "Supports"),
        listOf("CMS", "Supported"),
        listOf("CMS", "Supporting"),
        listOf("CMS", "Accepted"),
        listOf("CMS", "Accepting"),
        listOf("CMS", "Accepts"),
        listOf("FDA", "Approval"),
        listOf("FDA", "Authorization"),
        listOf("NDA", "Authorization"),
        listOf("Drug", "Authorization"),
        listOf("Drugs", "Authorization"),
        listOf("Food", "and", "Drug", "Authorization"),
        listOf("NDA", "Authority"),
        listOf("Drug", "Authority"),
        listOf("Drugs", "Authority"),
        listOf("Food", "and", "Drug", "Authority"),
        listOf("FDA", "Accept"),
        listOf("NDA", "Accept"),
        listOf("Drug", "Accept"),
        listOf("Drugs", "Accept"),
        listOf("Food", "and", "Drug", "Accept"),
        listOf("FDA", "Accepts"),
        listOf("NDA", "Accepts"),
        listOf("Drug", "Accepts"),
        listOf("Drugs", "Accepts"),
        listOf("Food", "and", "Drug", "Accepts"),
        listOf("FDA", "Accepting"),
        listOf("NDA", "Accepting"),
        listOf("Drug", "Accepting"),
        listOf("Drugs", "Accepting"),
        listOf("Food", "and", "Drug", "Accepting"),
        listOf("FDA", "Accepted"),
        listOf("NDA", "Accepted"),
        listOf("FDA", "Support"),
        listOf("FDA", "Supports"),
        listOf("FDA", "Supported"),
        listOf("FDA", "Supporting"),
        listOf("Food", "and", "Drug", "Support"),
        listOf("Food", "and", "Drug", "Supports"),
        listOf("Food", "and", "Drug", "Supported"),
        listOf("Food", "and", "Drug", "Supporting"),
        listOf("Drug", "Support"),
        listOf("Drug", "Supports"),
        listOf("Drug", "Supported"),
        listOf("Drug", "Supporting"),
        listOf("Drugs", "Support"),
        listOf("Drugs", "Supports"),
        listOf("Drugs", "Supported"),
        listOf("Drugs", "Supporting"),
        listOf("Drug", "Accepted"),
        listOf("Drugs", "Accepted"),
        listOf("Food", "and", "Drug", "Accepted"),
        listOf("Food", "and", "Drug", "Accept"),
        listOf("Food", "and", "Drug", "Authority"),
        listOf("Positive", "Phase"),
        listOf("Positive", "Phases"),
        listOf("Announces", "New", "Business"),
        listOf("Combination", "Planned"),
        listOf("Successful", "Devices"),
        listOf("Receives", "Accreditation"),
        listOf("Completed", "Project", "Successfully"),
        listOf("First", "Milestone", "Payment"),
        listOf("Deal", "Agreements"),
        listOf("Deal", "Agreement"),
        listOf("Deals", "Agreements"),
        listOf("Deals", "Agreement"),
        listOf("Food", "and", "Drug", "Approves"),
        listOf("Food", "and", "Drug", "Approval"),
        listOf("Food", "and", "Drug", "Approved"),
        listOf("Agreement", "Partnership"),
        listOf("Agreement", "Partnerships"),
        listOf("Distribution", "Agreement", "Signed"),
        listOf("Purchase", "Agreement"),
        listOf("Patents"),
        listOf("Patent"),
        listOf("Announces", "Positive", "Data"),
        listOf("Announces", "Agreement"),
        listOf("Announces", "Agreements"),
        listOf("Announces", "Sell"),
        listOf("Announces", "Sells"),
        listOf("FDA", "Clearance"),
        listOf("FDA", "Clearances"),
        listOf("Drugs", "Clearance"),
        listOf("Drug", "Clearances"),
        listOf("Announces", "Partnership"),
        listOf("Announces", "Partnerships"),
        listOf("Receives", "Clearance", "Phase"),
        listOf("Announces", "Sale", "Deal"),
        listOf("Announces", "Sale", "Deals"),
        listOf("Approves", "Drugs"),
        listOf("Approval", "Drugs"),
        listOf("Approved", "Drugs"),
        listOf("Approves", "Drug"),
        listOf("Approval", "Drug"),
        listOf("Approved", "Drug"),
        listOf("Merge", "Entry"),
        listOf("Merger", "Entry"),
        listOf("Merge", "Entries"),
        listOf("Merger", "Entries"),
        listOf("Merge", "Entering"),
        listOf("Merger", "Entering"),
        listOf("Announces", "Agreement"),
        listOf("Announces", "Business"),
        listOf("Product", "Launch"),
        listOf("Products", "Launch"),
        listOf("Breakthrough", "Designation"),
        listOf("Merger", "Announcement"),
        listOf("Acquisition", "Announcement"),
        listOf("Partnership", "Announcement"),
        listOf("Deploys", "Solution"),
        listOf("Deploys", "Solutions"),
        listOf("Deploys", "Platforms"),
        listOf("Deploys", "Platform"),
        listOf("Deploy", "Solution"),
        listOf("Deploy", "Solutions"),
        listOf("Deploy", "Platforms"),
        listOf("Deploy", "Platform"),
        listOf("Deploy", "Systems"),
        listOf("Deploy", "System"),
        listOf("Deploys", "Al"),
        listOf("Deploys", "Systems"),
        listOf("Deploys", "System"),
        listOf("Deploy", "Al"),
        listOf("Launchs", "Solution"),
        listOf("Launchs", "Solutions"),
        listOf("Launch", "Solution"),
        listOf("Launch", "Solutions"),
        listOf("Launchs", "Platforms"),
        listOf("Launchs", "Platform"),
        listOf("Launch", "Platforms"),
        listOf("Launch", "Platform"),
        listOf("Launch", "Systems"),
        listOf("Launch", "System"),
        listOf("Launchs", "Systems"),
        listOf("Launchs", "System"),
        listOf("Launch", "Al"),
        listOf("Launchs", "Al"),
        listOf("Starts", "Solution"),
        listOf("Starts", "Solutions"),
        listOf("Start", "Platforms"),
        listOf("Start", "Platform"),
        listOf("Starts", "Platforms"),
        listOf("Starts", "Platform"),
        listOf("Start", "Systems"),
        listOf("Start", "System"),
        listOf("Starts", "Al"),
        listOf("Start", "Al"),
        listOf("Starts", "Systems"),
        listOf("Starts", "System"),
        listOf("Enters", "Launch"),
        listOf("Entering", "Launch"),
        listOf("Entering", "Market"),
        listOf("Entering", "Markets"),
        listOf("Enters", "Market"),
        listOf("Enters", "Markets"),
        listOf("Entered", "Launch"),
        listOf("Entered", "Launched"),
        listOf("Entered", "Market"),
        listOf("Entered", "Markets"),
        listOf("Launched", "Solution"),
        listOf("Launched", "Solutions"),
        listOf("Launched", "Platforms"),
        listOf("Launched", "Platform"),
        listOf("Launched", "Systems"),
        listOf("Launched", "System"),
        listOf("Launched", "Al"),
        listOf("Started", "Solution"),
        listOf("Started", "Solutions"),
        listOf("Started", "Solution"),
        listOf("Started", "Solutions"),
        listOf("Started", "Platforms"),
        listOf("Started", "Platform"),
        listOf("Started", "Platforms"),
        listOf("Started", "Platform"),
        listOf("Started", "Systems"),
        listOf("Started", "System"),
        listOf("Started", "Al"),
        listOf("Starting", "Solution"),
        listOf("Starting", "Solutions"),
        listOf("Starting", "Platforms"),
        listOf("Starting", "Platform"),
        listOf("Starting", "Systems"),
        listOf("Starting", "System"),
        listOf("Starting", "Al"),
        listOf("Launching", "Solution"),
        listOf("Launching", "Solutions"),
        listOf("Launching", "Platforms"),
        listOf("Launching", "Platform"),
        listOf("Launching", "Systems"),
        listOf("Launching", "System"),
        listOf("Launching", "Al"),
        listOf("Deployed", "Solution"),
        listOf("Deployed", "Solutions"),
        listOf("Deployed", "Platforms"),
        listOf("Deployed", "Platform"),
        listOf("Deployed", "Systems"),
        listOf("Deployed", "System"),
        listOf("Deployed", "Al"),
        listOf("Deploying", "Solution"),
        listOf("Deploying", "Solutions"),
        listOf("Deploying", "Platforms"),
        listOf("Deploying", "Platform"),
        listOf("Deploying", "Systems"),
        listOf("Deploying", "System"),
        listOf("Deploying", "Al"),
        listOf("Groundbreaking"),
        listOf("Secures", "Financing"),
        listOf("Securing", "Financing"),
        listOf("Secured", "Financing"),
        listOf("Secure", "Financing"),
        listOf("Secures", "Financed"),
        listOf("Securing", "Financed"),
        listOf("Secured", "Financed"),
        listOf("Secure", "Financed"),
        listOf("Secures", "Finance"),
        listOf("Securing", "Finance"),
        listOf("Secured", "Finance"),
        listOf("Secure", "Finance"),
        listOf("Secures", "Finances"),
        listOf("Securing", "Finances"),
        listOf("Secured", "Finances"),
        listOf("Secure", "Finances"),
        listOf(" Acquires", "Rights"),
        listOf("Announces", "Plans", "Expansion"),
        listOf("Announces", "Plan", "Expansion"),
        listOf("GOVERNMENT", "CONTRACT"),
        listOf("US", "CONTRACT"),
        listOf("USA", "CONTRACT"),
        listOf("UK", "CONTRACT"),
        listOf("FDA", "APPROVES"),
        listOf("FDA", "APPROVED"),
        listOf("NDA", "APPROVAL"),
        listOf("CMS", "APPROVAL"),
        listOf("CMS", "APPROVES"),
        listOf("CMS", "APPROVED"),
        listOf("CMS", "ACCEPT"),
        listOf("CMS", "APPROVES"),
        listOf("CMS", "AUTHORITY"),
        listOf("CMS", "AUTHORIZATION"),
        listOf("CMS", "SUPPORT"),
        listOf("CMS", "SUPPORTS"),
        listOf("CMS", "SUPPORTED"),
        listOf("CMS", "SUPPORTING"),
        listOf("CMS", "ACCEPTED"),
        listOf("CMS", "ACCEPTING"),
        listOf("CMS", "ACCEPTS"),
        listOf("FDA", "APPROVAL"),
        listOf("FDA", "AUTHORIZATION"),
        listOf("NDA", "AUTHORIZATION"),
        listOf("DRUG", "AUTHORIZATION"),
        listOf("DRUGS", "AUTHORIZATION"),
        listOf("FOOD", "AND", "DRUG", "AUTHORIZATION"),
        listOf("NDA", "AUTHORITY"),
        listOf("DRUG", "AUTHORITY"),
        listOf("DRUGS", "AUTHORITY"),
        listOf("FOOD", "AND", "DRUG", "AUTHORITY"),
        listOf("FDA", "ACCEPT"),
        listOf("NDA", "ACCEPT"),
        listOf("DRUG", "ACCEPT"),
        listOf("DRUGS", "ACCEPT"),
        listOf("FOOD", "AND", "DRUG", "ACCEPT"),
        listOf("FDA", "ACCEPTS"),
        listOf("NDA", "ACCEPTS"),
        listOf("DRUG", "ACCEPTS"),
        listOf("DRUGS", "ACCEPTS"),
        listOf("FOOD", "AND", "DRUG", "ACCEPTS"),
        listOf("FDA", "ACCEPTING"),
        listOf("NDA", "ACCEPTING"),
        listOf("DRUG", "ACCEPTING"),
        listOf("DRUGS", "ACCEPTING"),
        listOf("FOOD", "AND", "DRUG", "ACCEPTING"),
        listOf("FDA", "ACCEPTED"),
        listOf("NDA", "ACCEPTED"),
        listOf("FDA", "SUPPORT"),
        listOf("FDA", "SUPPORTS"),
        listOf("FDA", "SUPPORTED"),
        listOf("FDA", "SUPPORTING"),
        listOf("FOOD", "AND", "DRUG", "SUPPORT"),
        listOf("FOOD", "AND", "DRUG", "SUPPORTS"),
        listOf("FOOD", "AND", "DRUG", "SUPPORTED"),
        listOf("FOOD", "AND", "DRUG", "SUPPORTING"),
        listOf("DRUG", "SUPPORT"),
        listOf("DRUG", "SUPPORTS"),
        listOf("DRUG", "SUPPORTED"),
        listOf("DRUG", "SUPPORTING"),
        listOf("DRUGS", "SUPPORT"),
        listOf("DRUGS", "SUPPORTS"),
        listOf("DRUGS", "SUPPORTED"),
        listOf("DRUGS", "SUPPORTING"),
        listOf("DRUG", "ACCEPTED"),
        listOf("DRUGS", "ACCEPTED"),
        listOf("FOOD", "AND", "DRUG", "ACCEPTED"),
        listOf("FOOD", "AND", "DRUG", "ACCEPT"),
        listOf("FOOD", "AND", "DRUG", "AUTHORITY"),
        listOf("POSITIVE", "PHASE"),
        listOf("POSITIVE", "PHASES"),
        listOf("ANNOUNCES", "NEW", "BUSINESS"),
        listOf("COMBINATION", "PLANNED"),
        listOf("SUCCESSFUL", "DEVICES"),
        listOf("RECEIVES", "ACCREDITATION"),
        listOf("COMPLETED", "PROJECT", "SUCCESSFULLY"),
        listOf("FIRST", "MILESTONE", "PAYMENT"),
        listOf("DEAL", "AGREEMENTS"),
        listOf("DEAL", "AGREEMENT"),
        listOf("DEALS", "AGREEMENTS"),
        listOf("DEALS", "AGREEMENT"),
        listOf("FOOD", "AND", "DRUG", "APPROVES"),
        listOf("FOOD", "AND", "DRUG", "APPROVAL"),
        listOf("FOOD", "AND", "DRUG", "APPROVED"),
        listOf("AGREEMENT", "PARTNERSHIP"),
        listOf("AGREEMENT", "PARTNERSHIPS"),
        listOf("DISTRIBUTION", "AGREEMENT", "SIGNED"),
        listOf("PURCHASE", "AGREEMENT"),
        listOf("PATENTS"),
        listOf("PATENT"),
        listOf("ANNOUNCES", "POSITIVE", "DATA"),
        listOf("ANNOUNCES", "AGREEMENT"),
        listOf("ANNOUNCES", "AGREEMENTS"),
        listOf("ANNOUNCES", "SELL"),
        listOf("ANNOUNCES", "SELLS"),
        listOf("FDA", "CLEARANCE"),
        listOf("FDA", "CLEARANCES"),
        listOf("DRUGS", "CLEARANCE"),
        listOf("DRUG", "CLEARANCES"),
        listOf("ANNOUNCES", "PARTNERSHIP"),
        listOf("ANNOUNCES", "PARTNERSHIPS"),
        listOf("RECEIVES", "CLEARANCE", "PHASE"),
        listOf("ANNOUNCES", "SALE", "DEAL"),
        listOf("ANNOUNCES", "SALE", "DEALS"),
        listOf("APPROVES", "DRUGS"),
        listOf("APPROVAL", "DRUGS"),
        listOf("APPROVED", "DRUGS"),
        listOf("APPROVES", "DRUG"),
        listOf("APPROVAL", "DRUG"),
        listOf("APPROVED", "DRUG"),
        listOf("MERGE", "ENTRY"),
        listOf("MERGER", "ENTRY"),
        listOf("MERGE", "ENTRIES"),
        listOf("MERGER", "ENTRIES"),
        listOf("MERGE", "ENTERING"),
        listOf("MERGER", "ENTERING"),
        listOf("ANNOUNCES", "AGREEMENT"),
        listOf("ANNOUNCES", "BUSINESS"),
        listOf("PRODUCT", "LAUNCH"),
        listOf("PRODUCTS", "LAUNCH"),
        listOf("BREAKTHROUGH", "DESIGNATION"),
        listOf("MERGER", "ANNOUNCEMENT"),
        listOf("ACQUISITION", "ANNOUNCEMENT"),
        listOf("PARTNERSHIP", "ANNOUNCEMENT"),
        listOf("DEPLOYS", "SOLUTION"),
        listOf("DEPLOYS", "SOLUTIONS"),
        listOf("DEPLOYS", "PLATFORMS"),
        listOf("DEPLOYS", "PLATFORM"),
        listOf("DEPLOY", "SOLUTION"),
        listOf("DEPLOY", "SOLUTIONS"),
        listOf("DEPLOY", "PLATFORMS"),
        listOf("DEPLOY", "PLATFORM"),
        listOf("DEPLOY", "SYSTEMS"),
        listOf("DEPLOY", "SYSTEM"),
        listOf("DEPLOYS", "AI"),
        listOf("DEPLOYS", "SYSTEMS"),
        listOf("DEPLOYS", "SYSTEM"),
        listOf("DEPLOY", "AI"),
        listOf("LAUNCHES", "SOLUTION"),
        listOf("LAUNCHES", "SOLUTIONS"),
        listOf("LAUNCH", "SOLUTION"),
        listOf("LAUNCH", "SOLUTIONS"),
        listOf("LAUNCHES", "PLATFORMS"),
        listOf("LAUNCHES", "PLATFORM"),
        listOf("LAUNCH", "PLATFORMS"),
        listOf("LAUNCH", "PLATFORM"),
        listOf("LAUNCH", "SYSTEMS"),
        listOf("LAUNCH", "SYSTEM"),
        listOf("LAUNCHES", "SYSTEMS"),
        listOf("LAUNCHES", "SYSTEM"),
        listOf("LAUNCH", "AI"),
        listOf("LAUNCHES", "AI"),
        listOf("STARTS", "SOLUTION"),
        listOf("STARTS", "SOLUTIONS"),
        listOf("START", "PLATFORMS"),
        listOf("START", "PLATFORM"),
        listOf("STARTS", "PLATFORMS"),
        listOf("STARTS", "PLATFORM"),
        listOf("START", "SYSTEMS"),
        listOf("START", "SYSTEM"),
        listOf("STARTS", "AI"),
        listOf("START", "AI"),
        listOf("STARTS", "SYSTEMS"),
        listOf("STARTS", "SYSTEM"),
        listOf("ENTERS", "LAUNCH"),
        listOf("ENTERING", "LAUNCH"),
        listOf("ENTERING", "MARKET"),
        listOf("ENTERING", "MARKETS"),
        listOf("ENTERS", "MARKET"),
        listOf("ENTERS", "MARKETS"),
        listOf("ENTERED", "LAUNCH"),
        listOf("ENTERED", "LAUNCHED"),
        listOf("ENTERED", "MARKET"),
        listOf("ENTERED", "MARKETS"),
        listOf("LAUNCHED", "SOLUTION"),
        listOf("LAUNCHED", "SOLUTIONS"),
        listOf("LAUNCHED", "PLATFORMS"),
        listOf("LAUNCHED", "PLATFORM"),
        listOf("LAUNCHED", "SYSTEMS"),
        listOf("LAUNCHED", "SYSTEM"),
        listOf("LAUNCHED", "AI"),
        listOf("STARTED", "SOLUTION"),
        listOf("STARTED", "SOLUTIONS"),
        listOf("STARTED", "SOLUTION"),
        listOf("STARTED", "SOLUTIONS"),
        listOf("STARTED", "PLATFORMS"),
        listOf("STARTED", "PLATFORM"),
        listOf("STARTED", "PLATFORMS"),
        listOf("STARTED", "PLATFORM"),
        listOf("STARTED", "SYSTEMS"),
        listOf("STARTED", "SYSTEM"),
        listOf("STARTED", "AI"),
        listOf("STARTING", "SOLUTION"),
        listOf("STARTING", "SOLUTIONS"),
        listOf("STARTING", "PLATFORMS"),
        listOf("STARTING", "PLATFORM"),
        listOf("STARTING", "SYSTEMS"),
        listOf("STARTING", "SYSTEM"),
        listOf("STARTING", "AI"),
        listOf("LAUNCHING", "SOLUTION"),
        listOf("LAUNCHING", "SOLUTIONS"),
        listOf("LAUNCHING", "PLATFORMS"),
        listOf("LAUNCHING", "PLATFORM"),
        listOf("LAUNCHING", "SYSTEMS"),
        listOf("LAUNCHING", "SYSTEM"),
        listOf("LAUNCHING", "AI"),
        listOf("DEPLOYED", "SOLUTION"),
        listOf("DEPLOYED", "SOLUTIONS"),
        listOf("DEPLOYED", "PLATFORMS"),
        listOf("DEPLOYED", "PLATFORM"),
        listOf("DEPLOYED", "SYSTEMS"),
        listOf("DEPLOYED", "SYSTEM"),
        listOf("DEPLOYED", "AI"),
        listOf("DEPLOYING", "SOLUTION"),
        listOf("DEPLOYING", "SOLUTIONS"),
        listOf("DEPLOYING", "PLATFORMS"),
        listOf("DEPLOYING", "PLATFORM"),
        listOf("DEPLOYING", "SYSTEMS"),
        listOf("DEPLOYING", "SYSTEM"),
        listOf("DEPLOYING", "AI"),
        listOf("GROUNDBREAKING"),
        listOf("SECURES", "FINANCING"),
        listOf("SECURING", "FINANCING"),
        listOf("SECURED", "FINANCING"),
        listOf("SECURE", "FINANCING"),
        listOf("SECURES", "FINANCED"),
        listOf("SECURING", "FINANCED"),
        listOf("SECURED", "FINANCED"),
        listOf("SECURE", "FINANCED"),
        listOf("SECURES", "FINANCE"),
        listOf("SECURING", "FINANCE"),
        listOf("SECURED", "FINANCE"),
        listOf("SECURE", "FINANCE"),
        listOf("SECURES", "FINANCES"),
        listOf("SECURING", "FINANCES"),
        listOf("SECURED", "FINANCES"),
        listOf("SECURE", "FINANCES"),
        listOf("ACQUIRES", "RIGHTS"),
        listOf("ANNOUNCES", "PLANS", "EXPANSION"),
        listOf("ANNOUNCES", "PLAN", "EXPANSION"),
        listOf("ANNOUNCES", "CONTRACT"),
        listOf("ANNOUNCES", "CONTRACTS"),
        listOf("STRATEGİC", "CONTRACT"),
        listOf("STRATEGİC", "CONTRACTS"),
        listOf("")
    )
    val resultBuilder = StringBuilder()

    fun anahtarKelimeKontrolu(metin: String): Boolean {
        for (liste in anahtarKelimeListeleri) {
            if (liste.all { anahtar -> metin.contains(anahtar) }) {
                return true
            }
        }
        return false
    }

    for (div in divs) {
        if ("OTC" in div.text()) continue
        if ("low float" !in div.text()) continue
        val timeElement = div.selectFirst("span.time")
        val timeText = timeElement?.text() ?: "Zaman bilgisi yok"
        val links = div.select("a.text-gray-dark.feed-link")
        for (link in links) {
            val href = link.attr("href")
            if ("/news" !in href) continue
            val cleanedHref = href.replace("/news/", "")
            if (cleanedHref.length < 40) continue
            val match = Pattern.compile("/([A-Z]{1,6})/").matcher(href)
            if (match.find()) {
                if (match.group(1).length >= 5 && match.group(1)[4] in listOf(
                        'F',
                        'Y',
                        'P',
                        'W'
                    )
                ) continue
                if (match.group(1).length == 6 && match.group(1)[5] in listOf(
                        'F',
                        'Y',
                        'P',
                        'W'
                    )
                ) continue
                var text = link.text()
                if (text in printedTexts || href in printedHrefs) continue
                text = "[$text] (${match.group(1)}) $timeText"
                printedTexts.add(text)
                printedHrefs.add(href)

                val bracketText = Pattern.compile("\\[(.*?)]").matcher(text)
                if (bracketText.find()) {
                    if (anahtarKelimeKontrolu(bracketText.group(1))) {
                        val result = text.replace("[", "{").replace("]", "}") + "\n\n"
                        resultBuilder.append(result)
                    }
                }
            }
        }
    }
    return resultBuilder.toString()
}



    val client = OkHttpClient()
    val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    val headers = mapOf(
        "Accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
        "Accept-Encoding" to "gzip, deflate, br",
        "Accept-Language" to "tr,en;q=0.9,en-GB;q=0.8,en-US;q=0.7",
        "Cache-Control" to "max-age=0",
        "If-Modified-Since" to "Mon, 22 Jan 2024 20:17:05 GMT",
        "Sec-Ch-Ua" to "\"Not_A Brand\";v=\"8\", \"Chromium\";v=\"120\", \"Microsoft Edge\";v=\"120\"",
        "Sec-Ch-Ua-Mobile" to "?0",
        "Sec-Ch-Ua-Platform" to "\"Windows\"",
        "Sec-Fetch-Dest" to "document",
        "Sec-Fetch-Mode" to "navigate",
        "Sec-Fetch-Site" to "none",
        "Sec-Fetch-User" to "?1",
        "Upgrade-Insecure-Requests" to "1",
        "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36 Edg/120.0.0.0"
    )

    data class BalanceSheetRow(val value1: String, val value2: String)
    data class BalanceSheetTable(val rows: List<BalanceSheetRow>)
    data class BalanceSheetData(val balanceSheetTable: BalanceSheetTable)
    data class BalanceSheetResponse(val data: BalanceSheetData)

    data class ShortInterestRow(val interest: String)
    data class ShortInterestTable(val rows: List<ShortInterestRow>)
    data class ShortInterestData(val shortInterestTable: ShortInterestTable)
    data class ShortInterestResponse(val data: ShortInterestData)



    fun getLatestAssetsLiabilities(ticker: String): Pair<Double?, Double?> {
        val url = "https://api.nasdaq.com/api/company/$ticker/financials?frequency=1"
        val headersBuilder = Headers.Builder()
        headers.forEach { (key, value) -> headersBuilder.add(key, value) }
        val request = Request.Builder().url(url).headers(headersBuilder.build()).build()
        val response = client.newCall(request).execute()
        val jsonAdapter = moshi.adapter(BalanceSheetResponse::class.java)
        val data = jsonAdapter.fromJson(response.body?.string())

        var assets: Double? = null
        var liabilities: Double? = null
        data?.data?.balanceSheetTable?.rows?.forEach { row ->
            when (row.value1) {
                "Total Liabilities & Equity" -> assets = row.value2.replace(",", "").replace("$", "").toDoubleOrNull()?.div(1000)
                "Total Liabilities" -> liabilities = row.value2.replace(",", "").replace("$", "").toDoubleOrNull()?.div(1000)
            }
        }

        return Pair(assets, liabilities)
    }

    fun getLatestInterest(ticker: String): Double? {
        val url = "https://api.nasdaq.com/api/quote/$ticker/short-interest?assetClass=stocks"
        val headersBuilder = Headers.Builder()
        headers.forEach { (key, value) -> headersBuilder.add(key, value) }
        val request = Request.Builder().url(url).headers(headersBuilder.build()).build()
        val response = client.newCall(request).execute()
        val jsonAdapter = moshi.adapter(ShortInterestResponse::class.java)
        val data = jsonAdapter.fromJson(response.body?.string())

        return data?.data?.shortInterestTable?.rows?.getOrNull(0)?.interest?.replace(",", "")?.toDoubleOrNull()
    }

    fun getLatestAssetsLiabilities2(ticker: String): Pair<Double?, Double?> {
        val url = "https://api.nasdaq.com/api/company/$ticker/financials?frequency=2"
        val headersBuilder = Headers.Builder()
        headers.forEach { (key, value) -> headersBuilder.add(key, value) }
        val request = Request.Builder().url(url).headers(headersBuilder.build()).build()
        val response = client.newCall(request).execute()
        val jsonAdapter = moshi.adapter(BalanceSheetResponse::class.java)
        val data = jsonAdapter.fromJson(response.body?.string())

        var assets: Double? = null
        var liabilities: Double? = null
        data?.data?.balanceSheetTable?.rows?.forEach { row ->
            when (row.value1) {
                "Total Liabilities & Equity" -> assets = row.value2.replace(",", "").replace("$", "").toDoubleOrNull()?.div(1000)
                "Total Liabilities" -> liabilities = row.value2.replace(",", "").replace("$", "").toDoubleOrNull()?.div(1000)
            }
        }

        return Pair(assets, liabilities)
    }

    data class StockInfo(val countryName: String, val marketCap: String, val price: String, val shares: String)

    fun getStockInfo(symbol: String): StockInfo {
        val apiKey = "clj0c5pr01qsgccbmjc0clj0c5pr01qsgccbmjcg"
        val quoteUrl = URL("https://finnhub.io/api/v1/quote?symbol=$symbol&token=$apiKey")
        val profileUrl = URL("https://finnhub.io/api/v1/stock/profile2?symbol=$symbol&token=$apiKey")

        val price = getStockInfo(quoteUrl, "c")
        val marketCap = getStockInfo(quoteUrl, "mc")
        val countryName = getStockInfo(profileUrl, "country")
        val shares = getStockInfo(profileUrl, "shareOutstanding")

        return StockInfo(countryName, marketCap, price, shares)
    }

    fun getStockInfo(url: URL, key: String): String {
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"

            BufferedReader(InputStreamReader(inputStream)).use {
                val response = it.readText()
                val json = JSONObject(response)
                return json.getString(key)
            }
        }
    }

private fun printBracketedText(text: String) {
    CoroutineScope(Dispatchers.IO).launch {
        fetchData()
    }
    val pattern = Pattern.compile("\\((.*?)\\)")
    val matcher = pattern.matcher(text)
    val bracketedTexts = mutableListOf<String>()
    while (matcher.find()) {
        bracketedTexts.add(matcher.group(1))
    }

    val curlyPattern = Pattern.compile("\\{(.*?)\\}")
    val curlyMatcher = curlyPattern.matcher(text)
    while (curlyMatcher.find()) {
        val curlyText = curlyMatcher.group(1)
        val innerMatcher = pattern.matcher(curlyText)
        while (innerMatcher.find()) {
            bracketedTexts.remove(innerMatcher.group(1))
        }

        for (text in bracketedTexts) {
            println(text)        }
    }

    for (symbol in bracketedTexts) {
        val stockInfo = getStockInfo(symbol)

        val marketCap = stockInfo.marketCap
        val price = stockInfo.price
        val shares = stockInfo.shares.toInt()
        val countryName = stockInfo.countryName

        var assets: Any? = null
        var liabilities: Any? = null
        try {
            val result = getLatestAssetsLiabilities(symbol)
            assets = result.first
            liabilities = result.second
        } catch (e: Exception) {
            try {
                val result = getLatestAssetsLiabilities2(symbol)
                assets = result.first
                liabilities = result.second
            } catch (e: Exception) {
                assets = null
                liabilities = null
            }
        }

        val shortInterest = getLatestInterest(symbol)
        val ratio = if (shares != 0 && shortInterest != null) (shortInterest / (shares * 1000000)) * 100 else 0

        if (assets == null || liabilities == null) {
            println("$symbol Unknown.")
        } else {
            println("[$symbol = ${marketCap.toInt()}M$ $symbol Fiyatı = $price$+ÜLKE = $countryName FLOAT = ${shares}M+$symbol = VARLIK: ${assets}M BORÇ: ${liabilities}M+$symbol = SHORT MİK: ${shortInterest?.div(1000000)?.let { "%.2f".format(it) }}M RATİO = ${"%.2f".format(ratio)}%+]")
        }
    }
}

fun a() {
    CoroutineScope(Dispatchers.IO).launch {
        printBracketedText("")
    }
}

fun main() {
    a()
}

