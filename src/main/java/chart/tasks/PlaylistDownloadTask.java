package chart.tasks;

import chart.ChartConfig;
import chart.spotify.SpotifyApi;
import chart.spotify.SpotifyConfig;
import com.google.common.collect.Lists;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.models.PlaylistTrack;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.SimplePlaylist;
import com.wrapper.spotify.models.Track;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

public class PlaylistDownloadTask {
    public static void main(String[] args) throws IOException, WebApiException {
        ChartConfig config = TaskUtils.getConfig();
        SpotifyConfig spotifyConfig = config.spotifyConfig();
        SpotifyApi api = SpotifyApi.create(spotifyConfig);
        String folder = config.csvDestination();
        Path file = Files.createFile(Paths.get(folder, "years6.csv"));


        List<SimplePlaylist> allPlaylists = api.getAllPlaylists();
        List<String> names = Lists.newArrayListWithExpectedSize(allPlaylists.size());
        allPlaylists.stream()
                .peek(pl -> names.add(pl.getName()))
                .filter(PlaylistDownloadTask::isYear)
                .forEach(year -> printPlaylist(api, year, file));

        names.forEach(System.out::println);
    }

    private static void printPlaylist(SpotifyApi api, SimplePlaylist year, Path file) {
        try (OutputStream fileStream = Files.newOutputStream(file, StandardOpenOption.APPEND);
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fileStream))) {
            printPlaylist(api, year, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void printPlaylist(SpotifyApi api, SimplePlaylist year, BufferedWriter writer) throws IOException {
        List<PlaylistTrack> tracks = api.getPlaylistTracks(year);
        System.out.println(year.getName() + ": " + tracks.size());
        int idx = 1;
        for (PlaylistTrack playlistTrack : tracks) {
            Track track = playlistTrack.getTrack();
            String trackName = track.getName();
            String albumName = track.getAlbum().getName();
            String artists = track.getArtists().stream().map(SimpleArtist::getName).collect(Collectors.joining(", "));
            String trackStr = String.format("%s\t%d\t%s\t%s\t%s",
                    year.getName(),
                    idx,
                    trackName,
                    artists,
                    albumName);
            writer.write(trackStr);
            writer.newLine();
        }
    }

    private static boolean isYear(SimplePlaylist simplePlaylist) {
        return simplePlaylist.getName().matches("[1-9][0-9][0-9][0-9]");
    }

}
