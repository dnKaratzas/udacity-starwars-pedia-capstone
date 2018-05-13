package eu.dkaratzas.starwarspedia.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.apollographql.apollo.api.Response;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import api.FilmQuery;
import api.PersonQuery;
import api.PlanetQuery;
import api.SpeciesQuery;
import api.StarshipQuery;
import api.VehicleQuery;
import eu.dkaratzas.starwarspedia.R;
import eu.dkaratzas.starwarspedia.api.SwapiCategory;
import timber.log.Timber;

public class AllQueryData implements Parcelable {
    private String id;
    private String title;
    private SwapiCategory category;
    private LinkedHashMap<String, String> detailsMap;
    private LinkedHashMap<String, List<QueryData>> relatedItems;

    public AllQueryData(Response response, Context context) {

        if (response.data() != null) {

            if (response.data() instanceof FilmQuery.Data) {
                mapFilmData(context, (FilmQuery.Data) response.data());
            } else if (response.data() instanceof PersonQuery.Data) {
                mapPersonData(context, (PersonQuery.Data) response.data());
            } else if (response.data() instanceof PlanetQuery.Data) {
                mapPlanetData(context, (PlanetQuery.Data) response.data());
            } else if (response.data() instanceof SpeciesQuery.Data) {
                mapSpeciesData(context, (SpeciesQuery.Data) response.data());
            } else if (response.data() instanceof StarshipQuery.Data) {
                mapStarshipsData(context, (StarshipQuery.Data) response.data());
            } else if (response.data() instanceof VehicleQuery.Data) {
                mapVehiclesData(context, (VehicleQuery.Data) response.data());
            } else {
                Timber.d("Unknown response.data instance.");
            }

        } else {
            Timber.d("response data is null");
        }
    }

    private void mapFilmData(Context context, FilmQuery.Data data) {
        FilmQuery.Film film = data.Film();

        if (film != null) {
            this.id = getSafeString(film.id());
            this.title = getSafeString(film.title());
            this.category = SwapiCategory.FILM;
            this.detailsMap = new LinkedHashMap<>();
            this.relatedItems = new LinkedHashMap<>();


            detailsMap.put(context.getString(R.string.release_date), getSafeString(DateFormat.getDateInstance(DateFormat.LONG).format(film.releaseDate())));
            detailsMap.put(context.getString(R.string.director), getSafeString(film.director()));
            detailsMap.put(context.getString(R.string.producer), getSafeString(film.producers()));
            detailsMap.put(context.getString(R.string.opening_crawl), getSafeString(film.openingCrawl()));

            if (film.characters() != null && film.characters().size() > 0) {
                List<QueryData> items = new ArrayList<>();
                for (FilmQuery.Character character : film.characters()) {
                    items.add(new QueryData(character.id(), character.name(), SwapiCategory.PEOPLE));
                }

                relatedItems.put(String.format(context.getString(R.string.related), context.getString(R.string.people)), items);
            }

            if (film.planets() != null && film.planets().size() > 0) {
                List<QueryData> items = new ArrayList<>();
                for (FilmQuery.Planet planet : film.planets()) {
                    items.add(new QueryData(planet.id(), planet.name(), SwapiCategory.PLANET));
                }

                relatedItems.put(String.format(context.getString(R.string.related), context.getString(R.string.planets)), items);
            }

            if (film.species() != null && film.species().size() > 0) {
                List<QueryData> items = new ArrayList<>();
                for (FilmQuery.Species species : film.species()) {
                    items.add(new QueryData(species.id(), species.name(), SwapiCategory.SPECIES));
                }

                relatedItems.put(String.format(context.getString(R.string.related), context.getString(R.string.species)), items);
            }

            if (film.starships() != null && film.starships().size() > 0) {
                List<QueryData> items = new ArrayList<>();
                for (FilmQuery.Starship starship : film.starships()) {
                    items.add(new QueryData(starship.id(), starship.name(), SwapiCategory.STARSHIP));
                }

                relatedItems.put(String.format(context.getString(R.string.related), context.getString(R.string.starships)), items);
            }

            if (film.vehicles() != null && film.vehicles().size() > 0) {
                List<QueryData> items = new ArrayList<>();
                for (FilmQuery.Vehicle vehicle : film.vehicles()) {
                    items.add(new QueryData(vehicle.id(), vehicle.name(), SwapiCategory.VEHICLE));
                }

                relatedItems.put(String.format(context.getString(R.string.related), context.getString(R.string.vehicles)), items);
            }
        }
    }

    private void mapPersonData(Context context, PersonQuery.Data data) {
        PersonQuery.Person person = data.Person();

        if (person != null) {
            this.id = getSafeString(person.id());
            this.title = getSafeString(person.name());
            this.category = SwapiCategory.PEOPLE;
            this.detailsMap = new LinkedHashMap<>();
            this.relatedItems = new LinkedHashMap<>();

            detailsMap.put(context.getString(R.string.birth_year), getSafeString(person.birthYear()));
            detailsMap.put(context.getString(R.string.height), getSafeString(person.height()));
            detailsMap.put(context.getString(R.string.mass), getSafeString(person.mass()));
            detailsMap.put(context.getString(R.string.gender), getSafeString(person.gender()));

            String hairColor = getListEnumValuesFormatted(person.hairColor());
            if (hairColor != null && !"".equals(hairColor))
                detailsMap.put(context.getString(R.string.hair_color), hairColor);

            String skinColor = getListEnumValuesFormatted(person.skinColor());
            if (skinColor != null && !"".equals(skinColor))
                detailsMap.put(context.getString(R.string.skin_color), skinColor);

            if (person.homeworld() != null) {
                List<QueryData> items = new ArrayList<>();

                items.add(new QueryData(person.homeworld().id(), person.homeworld().name(), SwapiCategory.PLANET));

                relatedItems.put(context.getString(R.string.homeworld), items);
            }

            if (person.films() != null && person.films().size() > 0) {
                List<QueryData> items = new ArrayList<>();
                for (PersonQuery.Film film : person.films()) {
                    items.add(new QueryData(film.id(), film.title(), SwapiCategory.FILM));
                }

                relatedItems.put(String.format(context.getString(R.string.related), context.getString(R.string.films)), items);
            }

            if (person.species() != null && person.species().size() > 0) {
                List<QueryData> items = new ArrayList<>();
                for (PersonQuery.Species species : person.species()) {
                    items.add(new QueryData(species.id(), species.name(), SwapiCategory.SPECIES));
                }

                relatedItems.put(String.format(context.getString(R.string.related), context.getString(R.string.species)), items);
            }

            if (person.starships() != null && person.starships().size() > 0) {
                List<QueryData> items = new ArrayList<>();
                for (PersonQuery.Starship starship : person.starships()) {
                    items.add(new QueryData(starship.id(), starship.name(), SwapiCategory.STARSHIP));
                }

                relatedItems.put(String.format(context.getString(R.string.related), context.getString(R.string.starships)), items);
            }

            if (person.vehicles() != null && person.vehicles().size() > 0) {
                List<QueryData> items = new ArrayList<>();
                for (PersonQuery.Vehicle vehicle : person.vehicles()) {
                    items.add(new QueryData(vehicle.id(), vehicle.name(), SwapiCategory.VEHICLE));
                }

                relatedItems.put(String.format(context.getString(R.string.related), context.getString(R.string.vehicles)), items);
            }
        }
    }

    private void mapPlanetData(Context context, PlanetQuery.Data data) {
        PlanetQuery.Planet planet = data.Planet();

        if (planet != null) {
            this.id = getSafeString(planet.id());
            this.title = getSafeString(planet.name());
            this.category = SwapiCategory.PLANET;
            this.detailsMap = new LinkedHashMap<>();
            this.relatedItems = new LinkedHashMap<>();

            detailsMap.put(context.getString(R.string.population), getSafeString(planet.population()));
            detailsMap.put(context.getString(R.string.rotation_period), getSafeString(planet.rotationPeriod()));
            detailsMap.put(context.getString(R.string.orbital_period), getSafeString(planet.orbitalPeriod()));
            detailsMap.put(context.getString(R.string.diameter), getSafeString(planet.diameter()));
            detailsMap.put(context.getString(R.string.gravity), getSafeString(planet.gravity()));
            detailsMap.put(context.getString(R.string.terrain), getSafeString(planet.terrain()));
            detailsMap.put(context.getString(R.string.surface_water), getSafeString(planet.surfaceWater()));
            detailsMap.put(context.getString(R.string.climate), getSafeString(planet.climate()));


            if (planet.residents() != null && planet.residents().size() > 0) {
                List<QueryData> items = new ArrayList<>();
                for (PlanetQuery.Resident resident : planet.residents()) {
                    items.add(new QueryData(resident.id(), resident.name(), SwapiCategory.PEOPLE));
                }

                relatedItems.put(context.getString(R.string.residents), items);
            }

            if (planet.films() != null && planet.films().size() > 0) {
                List<QueryData> items = new ArrayList<>();
                for (PlanetQuery.Film film : planet.films()) {
                    items.add(new QueryData(film.id(), film.title(), SwapiCategory.FILM));
                }

                relatedItems.put(String.format(context.getString(R.string.related), context.getString(R.string.films)), items);
            }


        }
    }

    private void mapSpeciesData(Context context, SpeciesQuery.Data data) {
        SpeciesQuery.Species species = data.Species();

        if (species != null) {
            this.id = getSafeString(species.id());
            this.title = getSafeString(species.name());
            this.category = SwapiCategory.SPECIES;
            this.detailsMap = new LinkedHashMap<>();
            this.relatedItems = new LinkedHashMap<>();

            detailsMap.put(context.getString(R.string.classification), getSafeString(species.classification()));
            detailsMap.put(context.getString(R.string.designation), getSafeString(species.designation()));
            detailsMap.put(context.getString(R.string.language), getSafeString(species.language()));
            detailsMap.put(context.getString(R.string.avg_lifespan), getSafeString(species.averageLifespan()));
            detailsMap.put(context.getString(R.string.avg_height), getSafeString(species.averageHeight()));

            String hairColor = getListEnumValuesFormatted(species.hairColor());
            if (hairColor != null && !"".equals(hairColor))
                detailsMap.put(context.getString(R.string.hair_color), hairColor);

            String skinColor = getListEnumValuesFormatted(species.skinColor());
            if (skinColor != null && !"".equals(skinColor))
                detailsMap.put(context.getString(R.string.skin_color), skinColor);

            String eyeColor = getListEnumValuesFormatted(species.eyeColor());
            if (eyeColor != null && !"".equals(eyeColor))
                detailsMap.put(context.getString(R.string.eye_color), eyeColor);

            if (species.people() != null && species.people().size() > 0) {
                List<QueryData> items = new ArrayList<>();
                for (SpeciesQuery.person people : species.people()) {
                    items.add(new QueryData(people.id(), people.name(), SwapiCategory.PEOPLE));
                }

                relatedItems.put(String.format(context.getString(R.string.related), context.getString(R.string.people)), items);
            }

            if (species.films() != null && species.films().size() > 0) {
                List<QueryData> items = new ArrayList<>();
                for (SpeciesQuery.Film film : species.films()) {
                    items.add(new QueryData(film.id(), film.title(), SwapiCategory.FILM));
                }

                relatedItems.put(String.format(context.getString(R.string.related), context.getString(R.string.films)), items);
            }
        }
    }

    private void mapStarshipsData(Context context, StarshipQuery.Data data) {
    }

    private void mapVehiclesData(Context context, VehicleQuery.Data data) {
    }

    private String getSafeString(Object value) {
        String result = "";
        if (value != null) {

            if (value instanceof String)
                result = (String) value;

            if (value instanceof Long || value instanceof Double)
                result = String.valueOf(value);

            if (value instanceof Enum)
                result = firstLetterCaps(value.toString());

            if (value instanceof List) {
                int count = 0;
                for (Object item : (List) value) {
                    if (item instanceof String) {
                        if (count != 0)
                            result += ", ";

                        result += firstLetterCaps((String) item);
                        count++;
                    }
                }
            }
        }
        return result;
    }

    private String getListEnumValuesFormatted(Object enumValues) {
        if (enumValues != null && enumValues instanceof List) {
            StringBuilder result = new StringBuilder();
            int count = 0;

            for (Object enumValue : (List) enumValues) {
                if (enumValue instanceof Enum) {
                    if (count != 0)
                        result.append(", ");

                    result.append(getSafeString(enumValue));
                    count++;
                }
            }
            return result.toString();
        }

        return null;
    }

    /**
     * Makes the first letter caps and the rest lowercase.
     * <p>
     * <p>
     * <p>
     * For example <code>fooBar</code> becomes <code>Foobar</code>.
     *
     * @param data capitalize this
     * @return String
     */
    private String firstLetterCaps(String data) {
        String firstLetter = data.substring(0, 1).toUpperCase();
        String restLetters = data.substring(1).toLowerCase();
        return firstLetter + restLetters;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LinkedHashMap<String, String> getDetailsMap() {
        return detailsMap;
    }

    public LinkedHashMap<String, List<QueryData>> getRelatedItems() {
        return relatedItems;
    }

    public SwapiCategory getCategory() {
        return category;
    }

    public StorageReference getImageStorageReference() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        switch (category) {
            case FILM:
                return storageRef.child("films/" + getId() + ".jpg");
            case PEOPLE:
                return storageRef.child("people/" + getId() + ".jpg");
            case PLANET:
                return storageRef.child("planets/" + getId() + ".jpg");
            case SPECIES:
                return storageRef.child("species/" + getId() + ".jpg");
            case STARSHIP:
                return storageRef.child("starships/" + getId() + ".jpg");
            case VEHICLE:
                return storageRef.child("vehicles/" + getId() + ".jpg");
        }
        return null;
    }

    // region Parcelable


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.title);
        dest.writeInt(this.detailsMap.size());
        for (Map.Entry<String, String> entry : this.detailsMap.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
        dest.writeInt(this.category == null ? -1 : this.category.ordinal());
        dest.writeInt(this.relatedItems.size());
        for (Map.Entry<String, List<QueryData>> entry : this.relatedItems.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeTypedList(entry.getValue());
        }
    }

    protected AllQueryData(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        int detailsMapSize = in.readInt();
        this.detailsMap = new LinkedHashMap<String, String>(detailsMapSize);
        for (int i = 0; i < detailsMapSize; i++) {
            String key = in.readString();
            String value = in.readString();
            this.detailsMap.put(key, value);
        }
        int tmpCategory = in.readInt();
        this.category = tmpCategory == -1 ? null : SwapiCategory.values()[tmpCategory];
        int relatedItemsSize = in.readInt();
        this.relatedItems = new LinkedHashMap<String, List<QueryData>>(relatedItemsSize);
        for (int i = 0; i < relatedItemsSize; i++) {
            String key = in.readString();
            List<QueryData> value = in.createTypedArrayList(QueryData.CREATOR);
            this.relatedItems.put(key, value);
        }
    }

    public static final Creator<AllQueryData> CREATOR = new Creator<AllQueryData>() {
        @Override
        public AllQueryData createFromParcel(Parcel source) {
            return new AllQueryData(source);
        }

        @Override
        public AllQueryData[] newArray(int size) {
            return new AllQueryData[size];
        }
    };
    // endregion
}
