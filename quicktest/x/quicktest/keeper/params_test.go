package keeper_test

import (
	"testing"

	"github.com/stretchr/testify/require"

	keepertest "quicktest/testutil/keeper"
	"quicktest/x/quicktest/types"
)

func TestGetParams(t *testing.T) {
	k, ctx := keepertest.QuicktestKeeper(t)
	params := types.DefaultParams()

	require.NoError(t, k.SetParams(ctx, params))
	require.EqualValues(t, params, k.GetParams(ctx))
}
