import React, { Component } from 'react';
import { CircleMarker, TileLayer, Tooltip, MapContainer, ZoomControl } from "react-leaflet";
import "leaflet/dist/leaflet.css";
import MapEventHandler from './MapEventHandler';

class BubbleMapDepartment extends Component {
  state = {
    countries: [],
    institutions: [],
    centerLat: 0,
    centerLong: 0,
    minLat: 90,
    maxLat: -90,
    minLong: 180,
    maxLong: -180,
    zoom: 2,
  };

  componentDidMount() {
    const { id } = this.props; 

    fetch(`http://localhost:8080/department/${id}/country`)
      .then(response => response.json())
      .then(data => {
        this.setCountryData(data);
      })
      .catch(error => console.error('Error fetching country data:', error));

    fetch(`http://localhost:8080/department/${id}/institutions`) // Use id from props
      .then(response => response.json())
      .then(data => {
        this.setInstitutionData(data);
      })
      .catch(error => console.error('Error fetching institution data:', error));
  }

  setCountryData = (data) => {
    const { minLat, maxLat, minLong, maxLong } = this.state;

    const updatedMinLat = data.reduce((min, country) => Math.min(min, country.averageLatitude), minLat);
    const updatedMaxLat = data.reduce((max, country) => Math.max(max, country.averageLatitude), maxLat);
    const updatedMinLong = data.reduce((min, country) => Math.min(min, country.averageLongitude), minLong);
    const updatedMaxLong = data.reduce((max, country) => Math.max(max, country.averageLongitude), maxLong);

    const updatedCenterLat = (updatedMinLat + updatedMaxLat) / 2;
    const updatedCenterLong = (updatedMinLong + updatedMaxLong) / 2;

    this.setState({
      countries: data,
      centerLat: updatedCenterLat,
      centerLong: updatedCenterLong,
      minLat: updatedMinLat,
      maxLat: updatedMaxLat,
      minLong: updatedMinLong,
      maxLong: updatedMaxLong,
    });
  };

  setInstitutionData = (data) => {
    this.setState({ institutions: data });
  };

  getColor = (articleCount) => {
    if (articleCount > 1000) return "#901212";
    if (articleCount > 500) return "#207ed5";
    if (articleCount > 100) return "#2a783c";
    if (articleCount > 50) return "#ceec5a";
    return "#2a2a78";
  };

  handleZoomEnd = (zoomLevel) => {
    this.setState({ zoom: zoomLevel });
  };

  render() {
    const { countries, institutions, centerLat, centerLong, minLat, maxLat, minLong, maxLong, zoom } = this.state;
    return (
      <div>
        <MapContainer
          style={{ height: "480px", width: "100%" }}
          zoom={zoom}
          center={[centerLat, centerLong]}
          bounds={[
            [minLat, minLong],
            [maxLat, maxLong]
          ]}
          zoomControl={false}
        >
          <ZoomControl position="topright" />
          <TileLayer url="http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
          <MapEventHandler onZoomEnd={this.handleZoomEnd} />

          {zoom > 3
            ? institutions
            .filter(institution => institution.x !== null && institution.y !== null && institution.institutionId !== 1)
            .map((institution, k) => (
                <CircleMarker
                  key={k}
                  center={[institution.x, institution.y]}
                  radius={10 * Math.log(institution.articleCount / 10 + 1)}
                  fillOpacity={0.5}
                  fillColor={this.getColor(institution.articleCount)}
                  stroke={false}
                >
                  <Tooltip direction="right" offset={[-8, -2]} opacity={1}>
                    <span>{`${institution.institutionName}: ${institution.articleCount}`}</span>
                  </Tooltip>
                </CircleMarker>
              ))
            : countries
            .filter(country => country.averageLatitude !== null && country.averageLongitude !== null && country.country !== "no country")
            .map((country, k) => (
                <CircleMarker
                  key={k}
                  center={[country.averageLatitude, country.averageLongitude]}
                  radius={10 * Math.log(country.totalArticles / 10 + 1)}
                  fillOpacity={0.5}
                  fillColor={this.getColor(country.totalArticles)}
                  stroke={false}
                >
                  <Tooltip direction="right" offset={[-8, -2]} opacity={1}>
                    <span>{`${country.country}: ${country.totalArticles}`}</span>
                  </Tooltip>
                </CircleMarker>
              ))
          }
        </MapContainer>
      </div>
    );
  }
}

export default BubbleMapDepartment;
