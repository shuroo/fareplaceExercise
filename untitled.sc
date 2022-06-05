

def formatDateParam(dateString: String): String = {
    if( dateString.charAt(4).equals('-') && dateString.charAt(7).equals('-')) {
        dateString
    }
    else{
        s"""${ dateString.substring(0,4) }-${ dateString.substring(4,6) }-${ dateString.substring(6,8) }"""
    }
}

print(formatDateParam("2020-01-01"))