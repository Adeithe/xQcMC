# xQc Auth

This plugin runs on a separate PaperMC/Spigot server running very few plugins.

When a player connects and Spigots `AsyncPlayerPreLoginEvent` triggers, the plugin sends a `POST` request to the URL specified in `config.yml` with the provided headers.

When the plugin receives a response, it will kick the player.
  - If `success` is set to `true` it will kick the player with the `code-generated` message set in `config.yml` replacing `${Token}` with `data.token` from the API response.
  - If `success` is set to `false` it will kick the player with the message set in `error.message` and include the status code set in `error.status` from the API response.
  - If an exception occurs, it will kick the player with the appropriate message set in `config.yml` and print the error to the console.

An important note is that this plugin will ignore the status code sent by the response completely.

Very little RAM is needed by this server as the player will never really see the world and it doesn't really matter much if the server lags. If you would like to take extra precaution to save RAM, use a [Void World](https://planetminecraft.com/blog/how-to-create-a-blank-world-void-world/) as your default spawn world so the server doesn't need to send chunk data to the player.

---

### POST Body

```json
  {
    "uuid": "string",
    "username": "string"
  }
```

---

### Response Body

The API is in charge of assigning a token to the provided UUID and returning it to display to the user.

The API should **NOT** return `data` if there is an error. You should never return both `data` and `error` in the same response.

```json
  {
    "success": true,
    "data": {
      "id": 0,
      "uuid": "string",
      "username": "string",
      "token": "string",
      "createdAt": "1970-01-01T00:00:00.000Z",
      "updatedAt": "1970-01-01T00:00:00.000Z"
    }
  }
```

```json
  {
    "success": false,
    "error": {
      "code": 0,
      "message": "string",
      "status": 401,
      "payload": {}
    }
  }
```