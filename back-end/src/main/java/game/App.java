package game;

import java.io.IOException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class App extends NanoHTTPD {

    public static void main(String[] args) {
        try {
            new App();
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
        }
    }

    private Game game;

    /**
     * Start the server at :8080 port.
     * @throws IOException
     */
    public App() throws IOException {
        super(8080);
        this.game = new Game();
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("\nRunning!\n");
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        Map<String, String> params = session.getParms();
        try {
            if (uri.equals("/newgame")) {
                this.game = new Game();  // Start a new game
            } else if (uri.equals("/play")) {
                // e.g., /play?x=1&y=1
                int x = Integer.parseInt(params.get("x"));
                int y = Integer.parseInt(params.get("y"));
                this.game = this.game.play(x, y);  // Make a move
            } else if (uri.equals("/undo")) {
                // Undo the last move
                this.game = this.game.undo();
            }
        } catch (Exception e) {
            return newFixedLengthResponse("Error: " + e.getMessage());
        }

        // Extract the view-specific data from the game and apply it to the template.
        GameState gameplay = GameState.forGame(this.game);
        return newFixedLengthResponse(gameplay.toString());
    }

    public static class Test {
        public String getText() {
            return "Hello World!";
        }
    }
}
