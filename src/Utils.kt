package geofflangenderfer

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper




fun String.asJson(): JsonNode = ObjectMapper().readTree(this)
