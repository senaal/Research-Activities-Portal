import React, { Component } from 'react';
import { CircleMarker, TileLayer, Tooltip, MapContainer } from "react-leaflet";
import "leaflet/dist/leaflet.css";

class BubbleMap extends Component {
  state = {
    institutions: [],
    centerLat: 0,
    centerLong: 0,
    minLat: 90,
    maxLat: -90,
    minLong: 180,
    maxLong: -180
  };

  componentDidMount() {
    fetch('http://localhost:8080/faculty/institutions')
      .then(response => response.json())
      .then(data => {
        this.setState({ institutions: data }, this.calculateBounds);
      })
      .catch(error => console.error('Error fetching data:', error));
  }

  calculateBounds = () => {
    const { institutions } = this.state;
    let minLat = 90, maxLat = -90, minLong = 180, maxLong = -180;

    institutions.forEach(institution => {
      const lat = institution.x;
      const long = institution.y;
      if (lat < minLat) minLat = lat;
      if (lat > maxLat) maxLat = lat;
      if (long < minLong) minLong = long;
      if (long > maxLong) maxLong = long;
    });

    const centerLat = (minLat + maxLat) / 2;
    const centerLong = (minLong + maxLong) / 2;
    const bufferLat = (maxLat - minLat) * 0.05;
    const bufferLong = (maxLong - minLong) * 0.05;

    this.setState({ centerLat, centerLong, minLat, maxLat, minLong, maxLong, bufferLat, bufferLong });
  };

  getColor = (articleCount) => {
    if (articleCount > 1000) return "#901212";
    if (articleCount > 500) return "#207ed5";
    if (articleCount > 100) return "#2a783c";
    if (articleCount > 50) return "#ceec5a";
    return "#2a2a78";
  };

  render() {
    const { institutions, centerLat, centerLong, minLat, maxLat, minLong, maxLong, bufferLat, bufferLong } = this.state;

    return (
      <div>
        <h3 style={{ textAlign: "center" }}>Work Counts With Universities</h3>
        <MapContainer
          style={{ height: "480px", width: "100%" }}
          zoom={2}
          center={[centerLat, centerLong]}
          bounds={[
            [minLat - bufferLat, minLong - bufferLong],
            [maxLat + bufferLat, maxLong + bufferLong]
          ]}
        >
          <TileLayer url="http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />

          {institutions.map((institution, k) => {
            if (institution.x === null || institution.y === null || institution.institutionId === 1) return null;
            return (
              <CircleMarker
                key={k}
                center={[institution.x, institution.y]}
                radius={20 * Math.log(institution.articleCount / 10 + 1)}
                fillOpacity={0.5}
                fillColor={this.getColor(institution.articleCount)}
                stroke={false}
              >
                <Tooltip direction="right" offset={[-8, -2]} opacity={1}>
                  <span>{institution.institutionName + ": " + institution.articleCount}</span>
                </Tooltip>
              </CircleMarker>
            )
          })}
        </MapContainer>
      </div>
    );
  }
}

export default BubbleMap;
