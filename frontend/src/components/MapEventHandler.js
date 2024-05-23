import { useEffect } from 'react';
import { useMap } from 'react-leaflet';

const MapEventHandler = ({ onZoomEnd }) => {
  const map = useMap();

  useEffect(() => {
    const handleZoomEnd = () => {
      onZoomEnd(map.getZoom());
    };

    map.on('zoomend', handleZoomEnd);

    return () => {
      map.off('zoomend', handleZoomEnd);
    };
  }, [map, onZoomEnd]);

  return null;
};

export default MapEventHandler;
