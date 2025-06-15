import React, { useEffect, useState } from 'react';
import {
  Box,
  Button,
  Card,
  CardActions,
  CardContent,
  Checkbox,
  FormControl,
  FormControlLabel,
  Grid,
  InputLabel,
  MenuItem,
  Paper,
  Select,
  TextField,
  Typography
} from '@mui/material';

const cardData = [
  {
    title: 'Spring Batch',
    method: 'springBatch',
    description: 'Ideal for large-scale ETL and chunk-based processing.'
  },
  {
    title: 'JDBC Batch',
    method: 'jdbcBatch',
    description: 'Uses batch inserts with plain JDBC, fastest in raw operations.'
  },
  {
    title: 'JPA',
    method: 'jpa',
    description: 'High-level ORM approach with convenience but slower for big volumes.'
  },
  // {
  //   title: 'Plain JDBC',
  //   method: 'plainJdbc',
  //   description: 'Manual control of JDBC, useful for detailed optimization.'
  // }
];

const BigData = () => {
  const [selectedMethods, setSelectedMethods] = useState(['springBatch', 'jdbcBatch', 'jpa', 'plainJdbc']);
  const [count, setCount] = useState(500000);
  const [type, setType] = React.useState(0);

  const handleChangeType = (event) => {
    setType(event.target.value);
  };

  const handleCheckboxChange = (method) => (e) => {
    setSelectedMethods((prev) =>
      e.target.checked
        ? [...prev, method]
        : prev.filter((m) => m !== method)
    );
  };
  useEffect(() => {
    console.log("selected method :::", selectedMethods);

  }, [selectedMethods])

  const handleStart = () => {
    const methodsToProcess = cardData.filter(({ method }) =>
      selectedMethods.includes(method)
    );
    console.log('🚀 Initiating Data Import:', {
      totalRecords: count,
      selectedStrategies: methodsToProcess.map(({ title }) => title)
    });
    // TODO: Connect with backend import logic per selected method
  };

  return (
    <Box sx={{ p: 2 }}>
      {/* Header */}
      <Grid
        marginBottom={2}
        container
        justifyContent="center"
        alignItems="center"
        padding="6px"
        borderRadius={3}
        sx={{ backgroundColor: '#1565C0' }}
      >
        <Typography fontWeight={600} color="white">
          Select & Run High-Volume Data Import Strategies
        </Typography>
      </Grid>

      {/* Common Input + Start Button */}
      <Paper sx={{ p: 2, mb: 2 }}>
        <Box sx={{ display: 'flex', gap: 2, alignItems: 'center', justifyContent: 'center' }}>
          <TextField
            value={count}
            onChange={(e) => setCount(Number(e.target.value))}
            type="number"
            size="small"
            label="Number of Data"
          />
          <Box sx={{ minWidth: 250 }}>
            <FormControl size='small' fullWidth>
              <InputLabel id="demo-simple-select-label">Type</InputLabel>
              <Select
                labelId="demo-simple-select-label"
                id="demo-simple-select"
                value={type}
                label="Age"
                onChange={handleChangeType}
              >
                <MenuItem value={0}>With Data Processing</MenuItem>
                <MenuItem value={1}>Without Processing</MenuItem>
              </Select>
            </FormControl>
          </Box>
          <Button variant="contained" sx={{ whiteSpace: 'nowrap' }} onClick={handleStart}>
            Start Selected
          </Button>
        </Box>
      </Paper>

      {/* Method Cards */}
      <Paper
        sx={{
          p: 2,
          display: 'flex',
          justifyContent: 'center',
          alignItems: 'stretch',
          flexWrap: 'wrap',
          gap: 2,
        }}
      >
        {cardData.map(({ title, method, description }) => (
          <Card
            key={method}
            sx={{
              width: 300, // fixed width
              display: 'flex',
              flexDirection: 'column',
              justifyContent: 'space-between',
              borderRadius: 4,
              boxShadow: '0 4px 20px rgba(0,0,0,0.1)',
              transition: 'transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out',
              background: 'linear-gradient(145deg, #f0f0f0, #ffffff)',
              '&:hover': {
                transform: 'translateY(-4px)',
                boxShadow: '0 8px 30px rgba(0,0,0,0.2)',
              },
              border: selectedMethods.includes(method)
                ? '2px solid #1976d2'
                : '1px solid #ccc',
            }}
          >
            <CardContent sx={{ flexGrow: 1 }}>
              <FormControlLabel
                control={
                  <Checkbox
                    checked={selectedMethods.includes(method)}
                    onChange={handleCheckboxChange(method)}
                  />
                }
                label={
                  <Typography fontWeight={600} fontSize="1rem">
                    {title}
                  </Typography>
                }
              />
              <Typography variant="body2" color="text.secondary">
                {description}
              </Typography>
            </CardContent>
            <CardActions>
              <Button size="small" disabled>
                Preview (Coming Soon)
              </Button>
            </CardActions>
          </Card>
        ))}
      </Paper>

    </Box>
  );
};

export default BigData;
