function inTable(tbl, item)
  for key, value in pairs(tbl) do
    if value == item then
      return key
    end
  end
end

CIDS = { 'GQE', 'XAF', 'XOF', 'BDT', 'XAF', 'GEL', 'HTG', 'KWD', 'LSL', 'MMK', 'XPF', 'WST', 'TND', 'RWF', 'PGK', 'DZD', 'XPF', 'GHS', 'GNF', 'LYD', 'XOF', 'STD', 'SLL', 'SDG', 'XOF', 'TMM', 'VEB', 'AMD', 'ETB', 'IQD', 'XOF', 'XOF', 'XAF', 'XOF', 'MGA', 'MDL', 'MAD', 'XOF', 'TOP', 'BTN', 'XAF', 'CDF', 'ERN', 'MVR', 'MOP', 'TRY', 'AED', 'ZMK', 'VUV', 'AOA', 'BHD', 'BIF', 'XAF', 'KMF', 'DJF', 'GMD', 'JOD', 'MWK', 'MRO', 'TJS', 'SDG', 'MZM', 'SZL', 'XPF', 'XOF', 'AFN', 'KHR', 'XCD', 'AUD', 'BRL', 'EUR', 'XCD', 'MYR', 'RSD', 'YER', 'BYR', 'INR', 'MUR', 'XCD', 'EUR', 'NOK', 'SEK', 'KZT', 'TZS', 'UZS', 'ARS', 'EUR', 'EUR', 'BOB', 'CZK', 'JMD', 'XCD', 'AZN', 'CNY', 'HKD', 'MKD', 'PKR', 'CRC', 'IRR', 'USD', 'CAD', 'EUR', 'LAK', 'PEN', 'QAR', 'SYP', 'NIO', 'SOS', 'EUR', 'COP', 'GBP', 'KYD', 'EGP', 'EUR', 'EUR', 'JPY', 'GIP', 'HUF', 'NPR', 'ILS', 'MNT', 'EUR', 'UAH', 'BGN', 'USD', 'AUD', 'AUD', 'BAM', 'CHF', 'RON', 'SRD', 'EUR', 'XCD', 'XCD', 'TWD', 'BBD', 'EUR', 'GTQ', 'LVL', 'PHP', 'ZAR', 'VND', 'CLP', 'HNL', 'EUR', 'SAR', 'AUD', 'ALL', 'DOP', 'IDR', 'KGS', 'USD', 'EUR', 'MXN', 'NZD', 'XCD', 'BSD', 'DKK', 'LRD', 'SHP', 'OMR', 'SBD', 'THB', 'SCR', 'BZD', 'KPW', 'USD', 'SGD', 'USD', 'BND', 'EUR', 'USD', 'EUR', 'PYG', 'LKR', 'HRK', 'AWG', 'FJD', 'EUR', 'GYD', 'KRW', 'EUR', 'KES', 'NAD', 'EUR', 'EUR', 'CHF', 'BWP', 'ISK', 'LBP', 'NGN', 'EUR', 'XCD', 'PLN', 'UYU', 'RUB', 'TTD', 'PAB', 'EUR', 'UGX', 'EUR', 'CUP' }

text = json.parse(request.body).text
from_to_table = {string.find(text,"(%u%u%u) (%u%u%u)")}
FROM = from_to_table[3] or 'USD'
TO = from_to_table[4] or 'CNY'
AMOUNT = string.sub(text, string.find(text,"%d+")) or "1"

if inTable(CIDS, FROM) and inTable(CIDS, TO) then
  response = http.request {
    url="http://www.freecurrencyconverterapi.com/api/v3/convert?q="..FROM.."_"..TO.."&compact=ultra"
  }
  result = json.parse(response.content)[FROM.."_"..TO] * tonumber(AMOUNT)
  return {text=AMOUNT.." "..FROM.." = "..result.." "..TO}
else
  return {text="输入格式不对，正确示例： 10 USD CNY"}
end
