# xQc Waterfall

This plugin is used by the Waterfall/BungeeCord server to handle connections.

When a player connects to the proxy and BungeeCords `PostLoginEvent` triggers, the plugin sends a `GET` request to the URL specified in `config.yml` with the provided headers.

When the plugin receives a response, it will check the HTTP Status Code.
  - If the status code is `401` it will kick the player with the `not-whitelisted` message set in the plugins `config.yml` file.
  - If the status code is anything other than `200` or if `success` is set to `false` it will kick the player with the message defined in `error.message` sent by the response.

---

### Response Body

The API should **NOT** return `data` if there is an error. You should never return both `data` and `error` in the same response.

```json
  {
    "success": true,
	"data": {
	  "id": 0,
	  "twitchId": "string",
	  "minecraftUuid": "string",
	  "minecraftUsername": "string",
	  "isSubbed": true,
	  "createdAt": "1970-01-01T00:00:00.000Z",
	  "updatedAt": "1970-01-01T00:00:00.000Z"
	}
  }
```

```json
  {
    "success": false,
	"error": {
	  "code": "string",
	  "message": "string",
	  "status": 401,
	  "payload": {}
	}
  }
```